
package com.bank.processing.dispatcher.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object JsonUtil {
    private val mapper = jacksonObjectMapper().registerKotlinModule()
    fun toJson(obj: Any): String = mapper.writeValueAsString(obj)
}
