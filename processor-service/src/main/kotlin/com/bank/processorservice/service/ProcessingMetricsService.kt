package com.bank.processorservice.service

import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ProcessingMetricsService(

    private val meterRegistry: MeterRegistry

) {

    private val logger = LoggerFactory.getLogger(ProcessingMetricsService::class.java)

    fun recordFileProcessing(
        fileName: String,
        totalTransactions: Int,
        startTime: Instant
    ) {

        val duration = Duration.between(startTime, Instant.now())
        val seconds = duration.toMillis() / 1000.0

        val throughputPerMinute =
            if (seconds == 0.0) totalTransactions.toDouble()
            else (totalTransactions / seconds) * 60

        // Micrometer metric
        meterRegistry.gauge(
            "bank.processing.throughput.per.minute",
            throughputPerMinute
        )

        meterRegistry.timer("bank.processing.file.duration")
            .record(duration)

        logger.info(
            """{
                "event":"FILE_PROCESSING_METRICS",
                "fileName":"$fileName",
                "totalTransactions":$totalTransactions,
                "durationMs":${duration.toMillis()},
                "throughputPerMinute":$throughputPerMinute
            }"""
        )
    }
}