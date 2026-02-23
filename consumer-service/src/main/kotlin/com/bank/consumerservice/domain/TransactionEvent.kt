package com.bank.consumerservice.domain

data class TransactionEvent(
    val transactionId: String,
    val transactionType: String,
    val amount: Double,
    val timestamp: String,
    val customerId: String,
    val metadata: String?,
    val processingTimestamp: String,
    val classification: String
)