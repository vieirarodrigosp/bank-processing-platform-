package com.bank.consumerservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class AwsConfig {

    @Bean
    fun dynamoDbClient(): DynamoDbClient =
        DynamoDbClient.builder().build()
}