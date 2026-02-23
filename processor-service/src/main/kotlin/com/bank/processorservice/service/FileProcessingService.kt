package com.bank.processorservice.service

import com.bank.processorservice.domain.ProcessingOutcome
import com.bank.processorservice.domain.ProcessingSummary
import com.bank.processorservice.repository.S3Repository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class FileProcessingService(

    private val idempotencyService: IdempotencyService,
    private val parameterStoreService: ParameterStoreService,
    private val s3Repository: S3Repository,
    private val csvProcessingService: CsvProcessingService,
    private val validationService: ValidationService,
    private val kafkaPublishService: KafkaPublishService,
    private val rejectionService: RejectionService,
    private val reportService: ReportService,
    private val circuitBreakerService: CircuitBreakerService,
    private val metricsService: ProcessingMetricsService

) {

    private val logger = LoggerFactory.getLogger(FileProcessingService::class.java)

    fun processFile(bucket: String, fileName: String) {

        val startTime = Instant.now()

        logger.info(
            """{"event":"PROCESS_START","fileName":"$fileName"}"""
        )

        // 1️⃣ Idempotência forte
        if (!idempotencyService.tryStartProcessing(fileName)) {
            return
        }

        // 2️⃣ Flag SSM
        if (!parameterStoreService.isProcessingEnabled()) {
            logger.warn(
                """{"event":"PROCESSING_DISABLED","fileName":"$fileName"}"""
            )
            return
        }

        val allowedTypes = parameterStoreService.loadRules()

        val summary = ProcessingSummary(fileName)

        circuitBreakerService.reset()

        var isFirstLine = true
        var stoppedByCircuitBreaker = false

        try {

            s3Repository.streamLines(bucket, fileName) { lineNumber, rawLine ->

                if (isFirstLine) {
                    isFirstLine = false
                    return@streamLines
                }

                if (circuitBreakerService.shouldStop()) {
                    stoppedByCircuitBreaker = true
                    return@streamLines
                }

                try {

                    val transaction =
                        csvProcessingService.parseLine(fileName, lineNumber, rawLine)

                    when (val outcome =
                        validationService.validate(
                            fileName,
                            lineNumber,
                            rawLine,
                            transaction,
                            allowedTypes
                        )
                    ) {

                        is ProcessingOutcome.Valid -> {
                            kafkaPublishService.publish(outcome.transaction)
                            summary.addValid(outcome.transaction)
                            circuitBreakerService.registerSuccess()
                        }

                        is ProcessingOutcome.Rejected -> {
                            rejectionService.collect(outcome)
                            summary.addRejected()
                            circuitBreakerService.registerFailure()
                        }
                    }

                } catch (ex: Exception) {

                    rejectionService.collectParsingError(
                        fileName,
                        lineNumber,
                        rawLine,
                        ex.message ?: "UNKNOWN_ERROR"
                    )

                    summary.addRejected()
                    circuitBreakerService.registerFailure()
                }
            }

        } finally {

            // Sempre executar
            rejectionService.flush(fileName)
            reportService.generate(summary)

            metricsService.recordFileProcessing(
                fileName,
                summary.validCount + summary.rejectedCount,
                startTime
            )
        }

        if (stoppedByCircuitBreaker) {
            logger.error(
                """{
                    "event":"PROCESS_STOPPED_BY_CIRCUIT_BREAKER",
                    "fileName":"$fileName",
                    "valid":${summary.validCount},
                    "rejected":${summary.rejectedCount}
                }"""
            )
        } else {
            logger.info(
                """{
                    "event":"PROCESS_COMPLETED",
                    "fileName":"$fileName",
                    "valid":${summary.validCount},
                    "rejected":${summary.rejectedCount}
                }"""
            )
        }
    }
}