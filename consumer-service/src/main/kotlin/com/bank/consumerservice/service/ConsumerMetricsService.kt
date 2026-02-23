package com.bank.consumerservice.service

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ConsumerMetricsService(
    private val meterRegistry: MeterRegistry
) {

    fun recordSuccess(start: Instant) {
        val duration = Duration.between(start, Instant.now())
        meterRegistry.timer("bank.consumer.processing.duration")
            .record(duration)
    }

    fun recordFailure() {
        meterRegistry.counter("bank.consumer.errors").increment()
    }
}