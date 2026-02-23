package com.bank.processorservice.service

import com.bank.processorservice.domain.Transaction
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Service
import java.io.StringReader
import java.time.Instant

@Service
class CsvProcessingService {

    fun parseLine(
        fileName: String,
        lineNumber: Int,
        rawLine: String
    ): Transaction {

        val parser = CSVParser(
            StringReader(rawLine),
            CSVFormat.DEFAULT
                .withTrim()
                .withIgnoreEmptyLines()
        )

        val record = parser.records.firstOrNull()
            ?: throw IllegalArgumentException("Empty CSV line")

        if (record.size() < 6) {
            throw IllegalArgumentException("INVALID_COLUMN_COUNT")
        }

        val transactionId = record.get(0)
        val transactionType = record.get(1)

        val amount = record.get(2).toDoubleOrNull()
            ?: throw IllegalArgumentException("INVALID_AMOUNT_FORMAT")

        val timestamp = record.get(3)
        val customerId = record.get(4)
        val metadata = record.get(5)

        return Transaction(
            transactionId = transactionId,
            transactionType = transactionType,
            amount = amount,
            timestamp = timestamp,
            customerId = customerId,
            metadata = metadata,
            processingTimestamp = Instant.now().toString(),
            classification = classify(amount)
        )
    }

    private fun classify(amount: Double): String =
        if (amount >= 0) "credit" else "debit"
}