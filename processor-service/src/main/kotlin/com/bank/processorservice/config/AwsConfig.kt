package com.bank.processorservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.ssm.SsmClient

@Configuration
class AwsConfig(
    @Value("\${aws.region}")
    private val awsRegion: String
) {

    private fun region() = Region.of(awsRegion)
    private fun credentials() = DefaultCredentialsProvider.create()

    @Bean
    fun s3Client(): S3Client =
        S3Client.builder()
            .region(region())
            .credentialsProvider(credentials())
            .build()

    @Bean
    fun dynamoDbClient(): DynamoDbClient =
        DynamoDbClient.builder()
            .region(region())
            .credentialsProvider(credentials())
            .build()

    @Bean
    fun ssmClient(): SsmClient =
        SsmClient.builder()
            .region(region())
            .credentialsProvider(credentials())
            .build()
}