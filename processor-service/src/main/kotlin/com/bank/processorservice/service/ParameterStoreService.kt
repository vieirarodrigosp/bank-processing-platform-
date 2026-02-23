package com.bank.processorservice.service

import com.bank.processorservice.config.ProcessingProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParameterRequest
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@Service
class ParameterStoreService(

    private val ssmClient: SsmClient,
    private val properties: ProcessingProperties

) {

    private val logger = LoggerFactory.getLogger(ParameterStoreService::class.java)

    private val cachedConfig = AtomicReference<SsmConfig>()
    private val lastLoadTime = AtomicReference<Instant>()

    data class SsmConfig(
        val processingEnabled: Boolean,
        val allowedTypes: Set<String>
    )

    fun isProcessingEnabled(): Boolean =
        loadConfig().processingEnabled

    fun loadRules(): Set<String> =
        loadConfig().allowedTypes

    private fun loadConfig(): SsmConfig {

        val now = Instant.now()
        val ttl = properties.ssm.refreshIntervalSeconds

        val lastLoad = lastLoadTime.get()
        val currentCache = cachedConfig.get()

        if (currentCache != null &&
            lastLoad != null &&
            Duration.between(lastLoad, now).seconds < ttl
        ) {
            return currentCache
        }

        synchronized(this) {

            val refreshedNow = Instant.now()
            val refreshedLastLoad = lastLoadTime.get()
            val refreshedCache = cachedConfig.get()

            if (refreshedCache != null &&
                refreshedLastLoad != null &&
                Duration.between(refreshedLastLoad, refreshedNow).seconds < ttl
            ) {
                return refreshedCache
            }

            logger.info("""{"event":"SSM_REFRESH"}""")

            val enabled = getParameter(properties.ssm.enabledFlag)
                .toBooleanStrictOrNull() ?: false

            val types = getParameter(properties.ssm.allowedTypes)
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()

            val newConfig = SsmConfig(
                processingEnabled = enabled,
                allowedTypes = types
            )

            cachedConfig.set(newConfig)
            lastLoadTime.set(refreshedNow)

            return newConfig
        }
    }

    private fun getParameter(name: String): String {

        val request = GetParameterRequest.builder()
            .name(name)
            .withDecryption(true)
            .build()

        return ssmClient.getParameter(request)
            .parameter()
            .value()
    }
}