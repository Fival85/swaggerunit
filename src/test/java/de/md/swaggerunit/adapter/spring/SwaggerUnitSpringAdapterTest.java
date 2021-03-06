package de.md.swaggerunit.adapter.spring;

import de.md.swaggerunit.adapter.spring.base.BaseTest;
import de.md.swaggerunit.core.SwaggerValidationException;
import de.md.swaggerunit.usage.SwaggerUnitRule;
import de.md.swaggerunit.usage.SwaggerValidation;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class SwaggerUnitSpringAdapterTest extends BaseTest {

	@Autowired
	@Rule
	public SwaggerUnitRule swaggerUnitRule;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Tests the spring integration with an invalid request to test-service and active swagger validation.
	 */
	@Test
	@SwaggerValidation
	public void testSpringIntegration() {
		client.exchange(createUri("test/simpleString"), HttpMethod.GET, null, String.class);
		thrown.expect(SwaggerValidationException.class);
	}
}
