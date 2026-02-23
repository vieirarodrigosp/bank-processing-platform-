package com.bank.processorservice.domain

import java.math.BigDecimal
import java.math.RoundingMode

class ProcessingSummary(

    val fileName: String

) {

    var validCount: Int = 0
        private set

    var rejectedCount: Int = 0
        private set

    private var totalAmount: BigDecimal = BigDecimal.ZERO

    private val totalsByType: MutableMap<String, Int> = mutableMapOf()

    fun addValid(transaction: Transaction) {
        validCount++
        totalAmount = totalAmount.add(BigDecimal.valueOf(transaction.amount))

        totalsByType.merge(
            transaction.transactionType,
            1,
            Int::plus
        )
    }

    fun addRejected() {
        rejectedCount++
    }

    fun totalProcessed(): Int =
        validCount + rejectedCount

    fun averageAmount(): BigDecimal =
        if (validCount == 0) BigDecimal.ZERO
        else totalAmount.divide(
            BigDecimal.valueOf(validCount.toLong()),
            2,
            RoundingMode.HALF_UP
        )

    fun totalsByType(): Map<String, Int> =
        totalsByType.toMap()

    fun errorRate(): Double =
        if (totalProcessed() == 0) 0.0
        else rejectedCount.toDouble() / totalProcessed().toDouble()
}