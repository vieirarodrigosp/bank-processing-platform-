package com.bank.consumerservice.service

import com.bank.consumerservice.domain.TransactionEvent
import com.bank.consumerservice.repository.DynamoRepository
import org.springframework.stereotype.Service

@Service
class TransactionPersistenceService(
    private val dynamoRepository: DynamoRepository
) {

    fun persist(event: TransactionEvent) {
        dynamoRepository.saveTransaction(event)
        dynamoRepository.updateSummary(event)
    }
}