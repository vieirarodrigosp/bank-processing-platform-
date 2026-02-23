package com.bank.processorservice.service

import com.bank.processorservice.config.ProcessingProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class CircuitBreakerService(

    private val properties: ProcessingProperties

) {

    private val logger = LoggerFactory.getLogger(CircuitBreakerService::class.java)

    private val successCount = AtomicInteger(0)
    private val failureCount = AtomicInteger(0)

    /**
     * Deve ser chamado no início de cada arquivo.
     */
    fun reset() {
        successCount.set(0)
        failureCount.set(0)
    }

    fun registerSuccess() {
        successCount.incrementAndGet()
    }

    fun registerFailure() {
        failureCount.incrementAndGet()
    }

    fun shouldStop(): Boolean {

        if (!properties.circuitBreaker.enabled) {
            return false
        }

        val success = successCount.get()
        val failure = failureCount.get()
        val total = success + failure

        if (total == 0) return false

        val errorRate = failure.toDouble() / total.toDouble()

        val threshold = properties.circuitBreaker.errorThreshold / 100.0

        val shouldStop = errorRate > threshold

        if (shouldStop) {
            logger.error(
                """{
                    "event":"CIRCUIT_BREAKER_OPEN",
                    "errorRate":$errorRate,
                    "threshold":$threshold,
                    "success":$success,
                    "failure":$failure
                }"""
            )
        }

        return shouldStop
    }
}