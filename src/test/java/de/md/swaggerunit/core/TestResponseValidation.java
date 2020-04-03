package de.md.swaggerunit.core;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fpriede on 26.04.2017.
 */
public class TestResponseValidation {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Injectable
	SwaggerUnitConfiguration swaggerUnitConfiguration;

	@Tested(fullyInitialized = true)
	SwaggerUnitCore swaggerUnitCore;

	@Before
	public void satisfyConstructionRequirements() {
		new Expectations() {
			{
				swaggerUnitConfiguration.getSwaggerSourceOverride();
				result = "swaggerDefinition2.yml";
			}
		};
	}

	/**
	 * Path was not found, so no validation took place.
	 */
	@Test
	public void testURINotInSwagger() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage("No API path found that matches request '/different/uri'");
		Map<String, List<String>> headers = new HashMap<>();
		swaggerUnitCore.validateResponse("GET", 200, URI.create("/different/uri"), headers, null);

	}

	/**
	 * Validation failed since empty Response body is not allowed.
	 */
	@Test(expected = SwaggerValidationException.class)
	public void testMissingResponseBody() {
		String responseBody = "{}";

		Map<String, List<String>> headers = new HashMap<>();

		swaggerUnitCore
				.validateResponse("GET", 200, URI.create("/v1/contracts/reactivation/MC.12345/check"), headers, responseBody);
	}

}
