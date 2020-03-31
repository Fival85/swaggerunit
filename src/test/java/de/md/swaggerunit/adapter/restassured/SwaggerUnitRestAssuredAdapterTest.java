package de.md.swaggerunit.adapter.restassured;

import de.md.swaggerunit.core.SwaggerValidationException;
import de.md.swaggerunit.usage.SwaggerUnitRule;
import de.md.swaggerunit.usage.SwaggerValidation;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;

public class SwaggerUnitRestAssuredAdapterTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this);
	@Rule
	public SwaggerUnitRule swaggerUnitRule;

	public SwaggerUnitRestAssuredAdapterTest() {
		SwaggerUnitRestAssuredAdapter adapter = new SwaggerUnitRestAssuredAdapter(new DefaultHttpClient());

		RestAssured.config = RestAssured.config()
				.httpClient(HttpClientConfig.httpClientConfig().httpClientFactory(adapter).reuseHttpClientInstance());
		SwaggerUnitRestAssuredConfigurationImpl config = new SwaggerUnitRestAssuredConfigurationImpl("simpleYaml.yml");

		swaggerUnitRule = new SwaggerUnitRule(adapter, config);
	}

	@SwaggerValidation
	@Test
	public void testRestAssuredIntegration() {
		when().get("http://localhost:" + mockServerRule.getPort() + "/notexisting");
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString("No API path found that matches request '/notexisting'."));

	}
}
