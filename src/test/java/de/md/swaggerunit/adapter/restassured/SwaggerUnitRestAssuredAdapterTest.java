package de.md.swaggerunit.adapter.restassured;

import de.md.swaggerunit.core.SwaggerValidationException;
import de.md.swaggerunit.usage.SwaggerUnitRule;
import de.md.swaggerunit.usage.SwaggerValidation;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.junit.MockServerRule;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class SwaggerUnitRestAssuredAdapterTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	@Rule
	public final MockServerRule mockServer = new MockServerRule(this);
	@Rule
	public final SwaggerUnitRule swaggerUnitRule;
	private final SwaggerUnitRestAssuredConfigurationImpl swaggerConfig;

	public SwaggerUnitRestAssuredAdapterTest() {
		SwaggerUnitRestAssuredAdapter adapter = new SwaggerUnitRestAssuredAdapter(new DefaultHttpClient());
		RestAssured.config = RestAssured.config()
				.httpClient(HttpClientConfig.httpClientConfig().httpClientFactory(adapter).reuseHttpClientInstance());
		swaggerConfig = new SwaggerUnitRestAssuredConfigurationImpl("simpleYaml.yml");
		swaggerUnitRule = new SwaggerUnitRule(adapter, swaggerConfig);
	}

	/**
	 * Simple integration test. The mockserver does not need to have any mocks, but should be active. The Validation should failed.
	 */
	@SwaggerValidation
	@Test
	public void testRestAssuredIntegration() {
		when().get("http://localhost:" + mockServer.getPort() + "/notexisting");
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString("No API path found that matches request '/notexisting'."));

	}

	/**
	 * Test the validation with an invalid request body.
	 */
	@SwaggerValidation
	@Test
	public void testRestAssuredIntegrationWithMissingRequestBody() {
		given().body("{\"contractId\" : \"MySpecialContract\"}").header("content-type", "application/json")
				.post("http://localhost:" + mockServer.getPort() + "/v1/post/withBody");
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(
				containsString("Object instance has properties which are not allowed by the schema: [\"contractId\"]"));
	}

	/**
	 * Test the validation with an invalid response body.
	 */
	@SwaggerValidation
	@Test
	public void testRestAssuredIntegrationWithBodyAndInvalidResponse() {
		final String requestPath = "/v1/post/withBody";
		final String requestBody = "{\"simpleField\" : \"MySpecial\"}";
		// mock
		mockServer.getClient().when(request().withBody(requestBody).withPath(requestPath).withMethod("POST"))
				.respond(response().withStatusCode(200).withBody("{\"unknownField\": \"val\"}"));
		//then
		given().body(requestBody).header("content-type", "application/json")
				.post("http://localhost:" + mockServer.getPort() + requestPath);
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(
				containsString("Object instance has properties which are not allowed by the schema: [\"unknownField\"]"));
	}
}
