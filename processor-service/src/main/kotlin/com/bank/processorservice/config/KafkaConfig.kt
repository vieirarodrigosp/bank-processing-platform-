package com.bank.processorservice.config

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig {

    private val logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {

        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()

        factory.consumerFactory = consumerFactory

        // Manual ACK (já configurado no YAML, mas reforçamos explicitamente)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL

        // Retry automático com backoff
        val errorHandler = DefaultErrorHandler(
            { record: ConsumerRecord<*, *>, exception: Exception ->
                logger.error(
                    """{"event":"KAFKA_CONSUME_ERROR","topic":"${record.topic()}","partition":${record.partition()},"offset":${record.offset()},"error":"${exception.message}"}"""
                )
            },
            FixedBackOff(2000L, 3) // 3 tentativas com 2s de intervalo
        )

        factory.setCommonErrorHandler(errorHandler)

        return factory
    }
}