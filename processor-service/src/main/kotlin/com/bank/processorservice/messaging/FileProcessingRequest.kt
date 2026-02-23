package com.bank.processorservice.messaging

data class FileProcessingRequest(

    val bucket: String,
    val fileName: String,
    val eventTimestamp: String,

    // opcional, mas boa prática
    val versionId: String? = null
)