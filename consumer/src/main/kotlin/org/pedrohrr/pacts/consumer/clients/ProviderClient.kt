package org.pedrohrr.pacts.consumer.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class ProviderClient(private val messagesUrl: String) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun getResponse(parameter: String): Response {
        val response = khttp.get(
                url = messagesUrl,
                params = mapOf("parameter" to parameter),
        ).text

        return objectMapper.readValue(response, Response::class.java)
    }
}

data class Response(val message: String)