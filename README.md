## Simple example of Pact

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

```sh
cd consumer
./gradlew clean build
```

After the build is successful, a pact file will be generated on `../pacts` folder, following the pattern `<consumer-name>-<provider-name>.json`.
All the interactions detailed on consumer will be described in this file.

### Validating the pact file

Now with a generated pact file, you have to verify if the provider can fulfill the requirements.

```sh
cd provider-spring
./gradlew clean build
```

In the build log you can assert that the interaction was properly implemented.