package org.pedrohrr.pacts.provider;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit5.AmpqTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@QuarkusTest
@Provider("message-provider")
@PactFolder("../pacts")
@ExtendWith({PactVerificationInvocationContextProvider.class})
public class MessageResourcePactProviderTest {

  @BeforeEach
  public void before(PactVerificationContext context) {
    context.setTarget(new AmpqTestTarget());
  }

  @TestTemplate
  public void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }
}
