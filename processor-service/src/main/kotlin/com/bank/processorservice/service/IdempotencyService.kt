package com.bank.processorservice.service

import com.bank.processorservice.repository.DynamoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class IdempotencyService(

    private val dynamoRepository: DynamoRepository

) {

    private val logger = LoggerFactory.getLogger(IdempotencyService::class.java)

    /**
     * Tenta adquirir lock do arquivo.
     *
     * Retorna true se pode processar.
     * Retorna false se já foi processado anteriormente.
     */
    fun tryStartProcessing(fileName: String): Boolean {

        val acquired = dynamoRepository.tryLockFile(fileName)

        if (!acquired) {
            logger.warn(
                """{"event":"FILE_PROCESSING_SKIPPED_ALREADY_PROCESSED","fileName":"$fileName"}"""
            )
        }

        return acquired
    }
}