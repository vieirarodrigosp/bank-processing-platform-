package com.bank.processorservice.repository

import com.bank.processorservice.domain.Transaction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.time.Instant

@Repository
class DynamoRepository(

    private val dynamoDbClient: DynamoDbClient,

    @Value("\${bank.processing.dynamodb.processed-files-table}")
    private val processedFilesTable: String,

    @Value("\${bank.processing.dynamodb.processed-transactions-table}")
    private val processedTransactionsTable: String

) {

    private val logger = LoggerFactory.getLogger(DynamoRepository::class.java)

    /**
     * Idempotência distribuída forte.
     * Retorna true se conseguiu adquirir lock.
     * Retorna false se arquivo já foi processado.
     */
    fun tryLockFile(fileName: String): Boolean {

        val item = mapOf(
            "fileName" to AttributeValue.fromS(fileName),
            "processedAt" to AttributeValue.fromS(Instant.now().toString())
        )

        val request = PutItemRequest.builder()
            .tableName(processedFilesTable)
            .item(item)
            .conditionExpression("attribute_not_exists(fileName)")
            .build()

        return try {
            dynamoDbClient.putItem(request)
            logger.info("""{"event":"FILE_LOCK_ACQUIRED","file":"$fileName"}""")
            true
        } catch (ex: ConditionalCheckFailedException) {
            logger.warn("""{"event":"FILE_ALREADY_PROCESSED","file":"$fileName"}""")
            false
        }
    }

    /**
     * Idempotência por transação.
     * Garante que a mesma transação não seja salva duas vezes.
     */
    fun saveTransaction(transaction: Transaction) {

        val item = mapOf(
            "transactionId" to AttributeValue.fromS(transaction.transactionId),
            "transactionType" to AttributeValue.fromS(transaction.transactionType),
            "amount" to AttributeValue.fromN(transaction.amount.toString()),
            "timestamp" to AttributeValue.fromS(transaction.timestamp),
            "customerId" to AttributeValue.fromS(transaction.customerId),
            "metadata" to AttributeValue.fromS(transaction.metadata ?: ""),
            "classification" to AttributeValue.fromS(transaction.classification),
            "processingTimestamp" to AttributeValue.fromS(transaction.processingTimestamp)
        )

        val request = PutItemRequest.builder()
            .tableName(processedTransactionsTable)
            .item(item)
            .conditionExpression("attribute_not_exists(transactionId)")
            .build()

        try {
            dynamoDbClient.putItem(request)
            logger.debug("""{"event":"TRANSACTION_SAVED","transactionId":"${transaction.transactionId}"}""")
        } catch (ex: ConditionalCheckFailedException) {
            logger.warn("""{"event":"TRANSACTION_DUPLICATE","transactionId":"${transaction.transactionId}"}""")
        }
    }
}