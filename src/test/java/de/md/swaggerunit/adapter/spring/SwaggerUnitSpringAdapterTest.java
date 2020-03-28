package de.md.swaggerunit.adapter.spring;

import de.md.swaggerunit.adapter.spring.base.BaseTest;
import de.md.swaggerunit.usage.SwaggerUnitRule;
import de.md.swaggerunit.usage.SwaggerValidation;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class SwaggerUnitSpringAdapterTest extends BaseTest {

	@Autowired
	@Rule
	public SwaggerUnitRule swaggerUnitRule;

	/**
	 * Tests the spring integration with an invalid request to test-service and active swagger validation.
	 * <p>
	 * To test, delete the @Ignore annotation.
	 * This test is positive if it fails, because the {@link SwaggerUnitRule} calls firstly {@link Statement#evaluate()} and
	 * after this the validation begins. So the unit test is already finished when the validation begins.
	 * This is because SwaggerUnit needs to wait until the request in the test is done.
	 * </p>
	 * TODO Any idea to fix this?
	 */
	@Test
	@Ignore
	@SwaggerValidation
	public void testSpringIntegration() {
		client.exchange(createUri("test/simpleString"), HttpMethod.GET, null, String.class);
	}
}
