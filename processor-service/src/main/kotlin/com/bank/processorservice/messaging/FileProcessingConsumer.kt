package com.bank.processorservice.messaging

import com.bank.processorservice.service.FileProcessingService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class FileProcessingConsumer(
    private val fileProcessingService: FileProcessingService
) {

    private val logger = LoggerFactory.getLogger(FileProcessingConsumer::class.java)

    @KafkaListener(
        topics = ["file.processing.request"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(
        record: ConsumerRecord<String, FileProcessingRequest>,
        ack: Acknowledgment
    ) {

        val request = record.value()
        val start = System.currentTimeMillis()

        logger.info(
            """{"event":"FILE_PROCESSING_RECEIVED","bucket":"${request.bucket}","fileName":"${request.fileName}","partition":${record.partition()},"offset":${record.offset()}}"""
        )

        try {

            fileProcessingService.processFile(
                bucket = request.bucket,
                fileName = request.fileName
            )

            ack.acknowledge()

            val duration = System.currentTimeMillis() - start

            logger.info(
                """{"event":"FILE_PROCESSING_COMPLETED","fileName":"${request.fileName}","durationMs":$duration}"""
            )

        } catch (ex: Exception) {

            logger.error(
                """{"event":"FILE_PROCESSING_FAILED","fileName":"${request.fileName}","error":"${ex.message}"}""",
                ex
            )

            // NÃO faz ack → Kafka retry via DefaultErrorHandler
            throw ex
        }
    }
}