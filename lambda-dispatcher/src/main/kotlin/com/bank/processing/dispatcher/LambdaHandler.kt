package com.bank.processing.dispatcher

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.bank.processing.dispatcher.model.FileProcessingRequest
import java.time.Instant

class LambdaHandler : RequestHandler<S3Event, String> {

    private val kafkaPublisher = KafkaPublisher()

    override fun handleRequest(event: S3Event, context: Context): String {

        val record = event.records.first()

        val bucket = record.s3.bucket.name

        val key = URLDecoder.decode(
            record.s3.`object`.key,
            StandardCharsets.UTF_8
        )

        val request = FileProcessingRequest(
            bucket = bucket,
            fileName = key,
            eventTimestamp = Instant.now().toString()
        )

        kafkaPublisher.publish(key, request)

        context.logger.log(
            """{"event":"FILE_PROCESSING_REQUEST_SENT","bucket":"$bucket","fileName":"$key"}"""
        )

        return "Processing started"
    }
}