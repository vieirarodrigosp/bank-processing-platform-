package com.bank.consumerservice.messaging

import com.bank.consumerservice.domain.TransactionEvent
import com.bank.consumerservice.service.CircuitBreakerService
import com.bank.consumerservice.service.ConsumerMetricsService
import com.bank.consumerservice.service.TransactionPersistenceService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TransactionConsumer(
    private val persistenceService: TransactionPersistenceService,
    private val circuitBreaker: CircuitBreakerService,
    private val metricsService: ConsumerMetricsService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${bank.processing.kafka.topic}"],
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(
        record: ConsumerRecord<String, TransactionEvent>,
        acknowledgment: Acknowledgment
    ) {

        val start = Instant.now()
        val event = record.value()

        if (circuitBreaker.isOpen()) {
            logger.error("""{"event":"CIRCUIT_OPEN"}""")
            throw IllegalStateException("Circuit open")
        }

        try {
            persistenceService.persist(event)
            acknowledgment.acknowledge()

            metricsService.recordSuccess(start)
            circuitBreaker.record(true)

        } catch (ex: Exception) {

            metricsService.recordFailure()
            circuitBreaker.record(false)

            logger.error(
                """{"event":"CONSUME_FAILED","transactionId":"${event.transactionId}"}""",
                ex
            )

            throw ex // ativa retry
        }
    }
}