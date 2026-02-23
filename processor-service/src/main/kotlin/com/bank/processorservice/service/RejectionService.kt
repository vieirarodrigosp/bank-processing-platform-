package com.bank.processorservice.service

import com.bank.processorservice.config.ProcessingProperties
import com.bank.processorservice.domain.ProcessingOutcome
import com.bank.processorservice.repository.S3Repository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

@Service
class RejectionService(

    private val properties: ProcessingProperties,
    private val s3Repository: S3Repository

) {

    private val logger = LoggerFactory.getLogger(RejectionService::class.java)

    private val rejectedRecords = ConcurrentLinkedQueue<RejectedRecord>()

    private val mapper = jacksonObjectMapper().registerKotlinModule()

    data class RejectedRecord(
        val fileName: String,
        val lineNumber: Int,
        val rawLine: String,
        val reason: String,
        val processingTimestamp: String
    )

    fun collect(outcome: ProcessingOutcome.Rejected) {
        rejectedRecords.add(
            RejectedRecord(
                fileName = outcome.fileName,
                lineNumber = outcome.lineNumber,
                rawLine = outcome.rawLine,
                reason = outcome.reason,
                processingTimestamp = outcome.processingTimestamp
            )
        )
    }

    fun collectParsingError(
        fileName: String,
        lineNumber: Int,
        rawLine: String,
        reason: String
    ) {
        rejectedRecords.add(
            RejectedRecord(
                fileName = fileName,
                lineNumber = lineNumber,
                rawLine = rawLine,
                reason = reason,
                processingTimestamp = Instant.now().toString()
            )
        )
    }

    fun flush(fileName: String) {

        if (rejectedRecords.isEmpty()) {
            return
        }

        val rejectedList = rejectedRecords.toList()

        val json = mapper.writeValueAsString(rejectedList)

        val rejectedBucket = properties.s3.rejectedBucket

        val rejectedFileName =
            "${fileName.removeSuffix(".csv")}-rejected-${Instant.now().toEpochMilli()}.json"

        s3Repository.saveObject(
            bucket = rejectedBucket,
            key = rejectedFileName,
            content = json
        )

        logger.info(
            """{"event":"REJECTIONS_SAVED","fileName":"$fileName","count":${rejectedList.size}}"""
        )

        rejectedRecords.clear()
    }
}