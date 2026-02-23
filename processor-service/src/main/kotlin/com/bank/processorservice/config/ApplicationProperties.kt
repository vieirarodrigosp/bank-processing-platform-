package com.bank.processorservice.config

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "bank.processing")
data class ProcessingProperties(

    val s3: S3Properties,
    val dynamodb: DynamoProperties,
    val kafka: KafkaProperties,
    val ssm: SsmProperties,
    val circuitBreaker: CircuitBreakerProperties,
    val retry: RetryProperties
) {

    data class S3Properties(
        @field:NotBlank
        val inputBucket: String,

        @field:NotBlank
        val rejectedBucket: String
    )

    data class DynamoProperties(
        @field:NotBlank
        val processedFilesTable: String,

        @field:NotBlank
        val processedTransactionsTable: String,

        @field:NotBlank
        val transactionSummaryTable: String
    )

    data class KafkaProperties(
        @field:NotBlank
        val topic: String
    )

    data class SsmProperties(
        @field:NotBlank
        val enabledFlag: String,

        @field:NotBlank
        val allowedTypes: String,

        @field:Min(1)
        val refreshIntervalSeconds: Long
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