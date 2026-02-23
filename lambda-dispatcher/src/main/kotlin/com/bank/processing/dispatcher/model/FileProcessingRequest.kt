package com.bank.processing.dispatcher.model

data class FileProcessingRequest(
    val bucket: String,
    val fileName: String,
    val eventTimestamp: String
)