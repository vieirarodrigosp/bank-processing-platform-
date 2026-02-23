package com.bank.processorservice.domain

sealed class ProcessingOutcome {

    data class Valid(
        val transaction: Transaction
    ) : ProcessingOutcome()

    data class Rejected(
        val fileName: String,
        val lineNumber: Int,
        val rawLine: String,
        val reason: String,
        val processingTimestamp: String
    ) : ProcessingOutcome()
}