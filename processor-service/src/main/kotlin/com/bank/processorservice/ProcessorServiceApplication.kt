package com.bank.processorservice

import com.bank.processorservice.config.ProcessingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ProcessingProperties::class)
class ProcessorServiceApplication

fun main(args: Array<String>) {
    runApplication<ProcessorServiceApplication>(*args)
}