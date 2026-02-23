package com.bank.consumerservice.config

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "bank.processing")
data class ProcessingProperties(

    val dynamodb: DynamoProperties,
    val kafka: KafkaProperties,
    val circuitBreaker: CircuitBreakerProperties,
    val retry: RetryProperties
) {

    data class DynamoProperties(

        @field:NotBlank
        val processedTransactionsTable: String,

        @field:NotBlank
        val transactionSummaryTable: String
    )

    data class KafkaProperties(

        @field:NotBlank
        val topic: String
    )

    data class CircuitBreakerProperties(

        @field:Min(1)
        val errorThreshold: Int,

        val enabled: Boolean
    )

    data class RetryProperties(

        @field:Min(1)
        val maxAttempts: Int
    )
}