package com.bank.processorservice.service

import com.bank.processorservice.config.ProcessingProperties
import com.bank.processorservice.domain.Transaction
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaPublishService(

    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val properties: ProcessingProperties

) {

    private val logger = LoggerFactory.getLogger(KafkaPublishService::class.java)

    fun publish(transaction: Transaction) {

        val topic = properties.kafka.topic
        val key = transaction.transactionId

        kafkaTemplate.send(topic, key, transaction)
            .whenComplete { result, ex ->

                if (ex != null) {
                    logger.error(
                        """{"event":"KAFKA_PUBLISH_FAILED","topic":"$topic","transactionId":"$key","error":"${ex.message}"}""",
                        ex
                    )
                } else {
                    logger.debug(
                        """{"event":"KAFKA_PUBLISH_SUCCESS","topic":"$topic","partition":${result.recordMetadata.partition()},"offset":${result.recordMetadata.offset()},"transactionId":"$key"}"""
                    )
                }
            }
    }
}