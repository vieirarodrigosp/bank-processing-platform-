package com.bank.processorservice.service

import com.bank.processorservice.domain.ProcessingOutcome
import com.bank.processorservice.domain.Transaction
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ValidationService {

    fun validate(
        fileName: String,
        lineNumber: Int,
        rawLine: String,
        transaction: Transaction,
        allowedTypes: Set<String>
    ): ProcessingOutcome {

        val now = Instant.now().toString()

        if (transaction.transactionId.isBlank() ||
            transaction.transactionType.isBlank() ||
            transaction.customerId.isBlank() ||
            transaction.timestamp.isBlank()
        ) {
            return ProcessingOutcome.Rejected(
                fileName = fileName,
                lineNumber = lineNumber,
                rawLine = rawLine,
                reason = "MISSING_REQUIRED_FIELD",
                processingTimestamp = now
            )
        }

        if (transaction.amount < 0) {
            return ProcessingOutcome.Rejected(
                fileName = fileName,
                lineNumber = lineNumber,
                rawLine = rawLine,
                reason = "NEGATIVE_AMOUNT",
                processingTimestamp = now
            )
        }

        if (!allowedTypes.contains(transaction.transactionType)) {
            return ProcessingOutcome.Rejected(
                fileName = fileName,
                lineNumber = lineNumber,
                rawLine = rawLine,
                reason = "TRANSACTION_TYPE_NOT_ALLOWED",
                processingTimestamp = now
            )
        }

        return ProcessingOutcome.Valid(transaction)
    }
}