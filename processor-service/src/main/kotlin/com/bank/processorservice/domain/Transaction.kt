package com.bank.processorservice.domain

data class Transaction(

    val transactionId: String,
    val transactionType: String,
    val amount: Double,
    val timestamp: String,
    val customerId: String,
    val metadata: String?,
    val classification: String,
    val processingTimestamp: String
)