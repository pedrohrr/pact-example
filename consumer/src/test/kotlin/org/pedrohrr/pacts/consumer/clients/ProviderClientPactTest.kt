package org.pedrohrr.pacts.consumer.clients

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "message-provider", port = "8888")
class ProviderClientPactTest {

    @Pact(provider = "message-provider", consumer = "message-consumer")
    fun createPact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
                .given("a valid message provider")
                .uponReceiving("a valid parameter")
                .path("/messages")
                .method("GET")
                .matchQuery("parameter", "\\w*", "someParameter")
                .willRespondWith()
                .status(200)
                .headers(mapOf("Content-type" to "application/json"))
                .body(PactDslJsonBody().stringType("message", "someMessage"))
                .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    fun getResponse(mockServer: MockServer) {
        val client = ProviderClient(mockServer.getUrl() + "/messages")
        val response = client.getResponse("someParameter")
        assertEquals(response.message, "someMessage")
    }
}