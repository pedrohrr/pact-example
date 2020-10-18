package org.pedrohrr.pacts.provider

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactFolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("message-provider")
@PactFolder("../pacts")
@ExtendWith(PactVerificationInvocationContextProvider::class)
class MessageProviderPactTest {

    @LocalServerPort
    var serverPort: Int = 0

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = HttpTestTarget(
                host = "localhost",
                port = serverPort,
                path = "/"
        )
    }

    @State("a valid message provider")
    fun setupProvider() {
    }

    @TestTemplate
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

}
