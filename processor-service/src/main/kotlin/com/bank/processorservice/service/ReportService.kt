package com.bank.processorservice.service

import com.bank.processorservice.config.ProcessingProperties
import com.bank.processorservice.domain.ProcessingSummary
import com.bank.processorservice.repository.S3Repository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ReportService(

    private val properties: ProcessingProperties,
    private val s3Repository: S3Repository

) {

    private val logger = LoggerFactory.getLogger(ReportService::class.java)

    private val mapper = jacksonObjectMapper().registerKotlinModule()

    data class Report(
        val fileName: String,
        val totalProcessed: Int,
        val validCount: Int,
        val rejectedCount: Int,
        val averageAmount: Double,
        val totalsByType: Map<String, Int>,
        val generatedAt: String
    )

    fun generate(summary: ProcessingSummary) {

        val report = Report(
            fileName = summary.fileName,
            totalProcessed = summary.totalProcessed(),
            validCount = summary.validCount,
            rejectedCount = summary.rejectedCount,
            averageAmount = summary.averageAmount().toDouble(),
            totalsByType = summary.totalsByType(),
            generatedAt = Instant.now().toString()
        )

        val json = mapper.writeValueAsString(report)

        val bucket = properties.s3.inputBucket // ou criar bucket específico para reports

        val reportFileName =
            "${summary.fileName.removeSuffix(".csv")}-report-${Instant.now().toEpochMilli()}.json"

        s3Repository.saveObject(
            bucket = bucket,
            key = reportFileName,
            content = json
        )

        logger.info(
            """{"event":"REPORT_GENERATED","fileName":"${summary.fileName}","valid":${summary.validCount},"rejected":${summary.rejectedCount}}"""
        )
    }
}