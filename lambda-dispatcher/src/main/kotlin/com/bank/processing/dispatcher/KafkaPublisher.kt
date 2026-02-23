package com.bank.processing.dispatcher

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class KafkaPublisher {

    private val topic = System.getenv("MSK_TOPIC_FILE_PROCESSING_REQUEST")
    private val producer: KafkaProducer<String, String>
    private val mapper = jacksonObjectMapper().registerKotlinModule()

    init {
        val props = Properties()

        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] =
            System.getenv("MSK_BOOTSTRAP_SERVERS")

        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java.name

        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java.name

        props["security.protocol"] = "SASL_SSL"
        props["sasl.mechanism"] = "AWS_MSK_IAM"
        props["sasl.jaas.config"] =
            "software.amazon.msk.auth.iam.IAMLoginModule required;"
        props["sasl.client.callback.handler.class"] =
            "software.amazon.msk.auth.iam.IAMClientCallbackHandler"

        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        props[ProducerConfig.RETRIES_CONFIG] = 3
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 5

        producer = KafkaProducer(props)
    }

    fun publish(key: String, payload: Any) {
        val json = mapper.writeValueAsString(payload)
        producer.send(ProducerRecord(topic, key, json))
    }
}