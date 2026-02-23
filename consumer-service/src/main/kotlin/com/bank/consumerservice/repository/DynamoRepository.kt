package com.bank.consumerservice.repository

import com.bank.consumerservice.domain.TransactionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Repository
class DynamoRepository(

    private val dynamoDbClient: DynamoDbClient,

    @Value("\${bank.processing.dynamodb.processed-transactions-table}")
    private val processedTransactionsTable: String,

    @Value("\${bank.processing.dynamodb.transaction-summary-table}")
    private val transactionSummaryTable: String

) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Idempotência forte por transactionId
     */
    fun saveTransaction(event: TransactionEvent) {

        val item = mapOf(
            "transactionId" to AttributeValue.fromS(event.transactionId),
            "transactionType" to AttributeValue.fromS(event.transactionType),
            "amount" to AttributeValue.fromN(event.amount.toString()),
            "timestamp" to AttributeValue.fromS(event.timestamp),
            "customerId" to AttributeValue.fromS(event.customerId),
            "metadata" to AttributeValue.fromS(event.metadata ?: ""),
            "classification" to AttributeValue.fromS(event.classification),
            "processingTimestamp" to AttributeValue.fromS(event.processingTimestamp)
        )

        val request = PutItemRequest.builder()
            .tableName(processedTransactionsTable)
            .item(item)
            .conditionExpression("attribute_not_exists(transactionId)")
            .build()

        try {
            dynamoDbClient.putItem(request)

            logger.debug(
                """{"event":"TRANSACTION_SAVED","transactionId":"${event.transactionId}"}"""
            )

        } catch (ex: ConditionalCheckFailedException) {

            logger.warn(
                """{"event":"TRANSACTION_DUPLICATE","transactionId":"${event.transactionId}"}"""
            )
        }
    }

    /**
     * Atualiza consolidação incremental
     * Usa ADD para contadores atômicos
     */
    fun updateSummary(event: TransactionEvent) {

        val date = LocalDate.now(ZoneOffset.UTC).toString()

        val key = mapOf(
            "date" to AttributeValue.fromS(date),
            "transactionType" to AttributeValue.fromS(event.transactionType)
        )

        val updateExpression = """
            ADD totalCount :inc,
                totalAmount :amount
        """.trimIndent()

        val expressionValues = mapOf(
            ":inc" to AttributeValue.fromN("1"),
            ":amount" to AttributeValue.fromN(event.amount.toString())
        )

        val request = UpdateItemRequest.builder()
            .tableName(transactionSummaryTable)
            .key(key)
            .updateExpression(updateExpression)
            .expressionAttributeValues(expressionValues)
            .build()

        dynamoDbClient.updateItem(request)

        logger.debug(
            """{"event":"SUMMARY_UPDATED","type":"${event.transactionType}"}"""
        )
    }
}