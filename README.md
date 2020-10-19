## Simple example of pact validation

[![Build Status](https://travis-ci.org/pedrohrr/pact-example.svg?branch=main)](https://travis-ci.org/pedrohrr/pact-example)

An extremely simple example of how to implement pact validation between two microservices.
Even though it is simple, it covers the complete flow of a pact, with working examples of both consumer and provider.

#### Consumer was build with:
- [kotlin](https://kotlinlang.org/)
- [junit5](https://junit.org/junit5/)
- [khttp](https://khttp.readthedocs.io/en/latest/)
- [jackson](https://github.com/FasterXML/jackson) 
- [pact-jvm-consumer](https://github.com/DiUS/pact-jvm/tree/master/consumer)

#### Provider was build with:
- [kotlin](https://kotlinlang.org/)
- [junit5](https://junit.org/junit5/)
- [spring-boot](https://spring.io/projects/spring-boot)
- [jackson](https://github.com/FasterXML/jackson) 
- [assertj](https://joel-costigliola.github.io/assertj/)
- [pact-jvm-provider](https://github.com/DiUS/pact-jvm/tree/master/provider)

### Generating the pact file

The validation of a pact starts by creating the pact from the consumer perspective.

- Adding dependencies
```kts
repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("khttp:khttp:0.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.assertj:assertj-core:3.9.1")
    testImplementation("au.com.dius.pact.consumer:junit5:4.1.0")
}
```

- Specifying folder to generate pacts
```kts
tasks.withType<Test> {
    useJUnitPlatform()
    systemProperties["pact.rootDir"] = "$buildDir/../../pacts"
}
```

- Creating pact test
```kt
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
```

- Implementing client
```kt
class ProviderClient(private val messagesUrl: String) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun getResponse(parameter: String): Response {
        val response = khttp.get(
                url = messagesUrl,
                params = mapOf("parameter" to parameter)
        ).text

        return objectMapper.readValue(response, Response::class.java)
    }
}

data class Response(val message: String)
```

- Running tests
```sh
cd consumer
./gradlew test
```

After the test is successful, a pact file will be generated on `../pacts` folder, following the pattern `<consumer-name>-<provider-name>.json`.
All the interactions detailed on consumer will be described in this file.

### Validating the pact file

Now with a generated pact file, you have to verify if the provider can fulfill the requirements.

- Adding dependencies
```kts
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
	testImplementation("org.assertj:assertj-core:3.9.1")
	testImplementation("au.com.dius.pact.provider:junit5:4.1.0")
}
```

- Writing pact provider test
```kt
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
```

- Implementing the endpoint for interaction
```kt
@RestController
@RequestMapping("/messages")
class MessageResource {

    @GetMapping
    fun getMessage(@RequestParam("parameter") parameter: String): Response {
        return Response("any message in here")
    }

}

data class Response(val message: String)
```

- Verifying the pacts
```sh
cd provider-spring
./gradlew test
```

In the build log you can assert that the interaction was properly implemented.
A better understanding is achieved by modifying both sides and running the test again.

## How to share the pacts with no physical dependencies?

For that you will need to implement a [pact-broker](https://github.com/pact-foundation/pact_broker). 
Pact-broker will allow you to share the pacts and verification results if needed.
