package com.bank.processorservice.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InputStream

@Repository
class S3Repository(

    private val s3Client: S3Client

) {

    private val logger = LoggerFactory.getLogger(S3Repository::class.java)

    /**
     * Retorna InputStream para processamento streaming.
     */
    fun download(bucket: String, fileName: String): InputStream {

        val request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .build()

        logger.info("""{"event":"S3_DOWNLOAD_STARTED","bucket":"$bucket","file":"$fileName"}""")

        return s3Client.getObject(request)
    }

    /**
     * Salva qualquer conteúdo JSON no S3.
     */
    fun saveObject(
        bucket: String,
        key: String,
        content: String
    ) {

        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType("application/json")
            .build()

        s3Client.putObject(
            request,
            RequestBody.fromString(content)
        )

        logger.info("""{"event":"S3_OBJECT_SAVED","bucket":"$bucket","key":"$key"}""")
    }

    /**
     * Helper opcional para leitura linha a linha.
     */
    fun streamLines(
        bucket: String,
        fileName: String,
        consumer: (lineNumber: Int, line: String) -> Unit
    ) {

        download(bucket, fileName).use { inputStream ->

            BufferedReader(InputStreamReader(inputStream)).useLines { lines ->

                var lineNumber = 0

                lines.forEach { line ->
                    lineNumber++
                    consumer(lineNumber, line)
                }
            }
        }
    }
}