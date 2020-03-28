package de.md.swaggerunit.adapter.spring;

import de.md.swaggerunit.adapter.spring.base.BaseTest;
import de.md.swaggerunit.usage.SwaggerUnitRule;
import de.md.swaggerunit.usage.SwaggerValidation;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class SwaggerUnitSpringAdapterTest extends BaseTest {

	@Autowired
	@Rule
	public SwaggerUnitRule swaggerUnitRule;

	/**
	 * Tests the spring integration with an invalid request to test-service and aktive swagger validation.
	 */
	@Test(expected = de.md.swaggerunit.core.SwaggerValidationException.class)
	@SwaggerValidation
	public void testMonoWithSimpleString() {
		client.exchange(createUri("test/simpleString"), HttpMethod.GET, null, String.class);
	}
}
