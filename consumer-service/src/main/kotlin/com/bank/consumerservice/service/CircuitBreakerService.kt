package com.bank.consumerservice.service

import com.bank.consumerservice.config.ProcessingProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class CircuitBreakerService(
    private val properties: ProcessingProperties
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val totalRequests = AtomicInteger(0)
    private val errorRequests = AtomicInteger(0)

    /**
     * Registra sucesso ou erro
     */
    fun record(success: Boolean) {
        totalRequests.incrementAndGet()
        if (!success) {
            errorRequests.incrementAndGet()
        }
    }

    /**
     * Retorna true se o circuito estiver aberto
     */
    fun isOpen(): Boolean {

        if (!properties.circuitBreaker.enabled) {
            return false
        }

        val total = totalRequests.get()

        // Evita abrir com poucas amostras
        if (total < 50) {
            return false
        }

        val errors = errorRequests.get()
        val errorRate = (errors.toDouble() / total) * 100

        val threshold = properties.circuitBreaker.errorThreshold

        if (errorRate > threshold) {

            logger.error(
                """{
                    "event":"CIRCUIT_OPEN",
                    "errorRate":$errorRate,
                    "threshold":$threshold
                }"""
            )

            return true
        }

        return false
    }

    /**
     * Reset manual (opcional)
     */
    fun reset() {
        totalRequests.set(0)
        errorRequests.set(0)
    }
}