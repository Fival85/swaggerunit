package de.md.swaggerunit.core;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;

public class TestRequestValidation {

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
				result = "swaggerDefinition1.yml";
			}
		};
	}

	@Test
	public void testValidation_shouldPass() {
		URI toTest = URI.create("/v1/contracts/tariffSwap?contractId=mc.123324");

		@SuppressWarnings("serial") Map<String, List<String>> headers = new HashMap<String, List<String>>() {{
			put("Channel", Collections.singletonList("App"));
			put("Agent", Collections.singletonList("Fox Mulder"));
		}};

		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

	@Test
	public void oneHeaderMissing_shouldFail() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString(
				"Header parameter 'Agent' is required on path '/v1/contracts/tariffSwap' but not found in request. Mandatory header \"Agent\" is not set."));
		URI toTest = URI.create("/v1/contracts/tariffSwap?contractId=mc.123324");
		@SuppressWarnings("serial") Map<String, List<String>> headers = new HashMap<String, List<String>>() {
			{
				put("Channel", Collections.singletonList("App"));
			}
		};
		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

	/**
	 * Tests if per default every request will be validate, even if it is not in the swagger definition.
	 */
	@Test
	public void testMissingPathWithoutIgnoringUnknownPaths() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString("No API path found that matches request '/v1/notexisting'."));
		URI toTest = URI.create("/v1/notexisting");
		swaggerUnitCore.validateRequest("GET", toTest, null, "{}");
	}

	/**
	 * Tests if an unknown path call will not be ignored by swaggerunit, if the configuration key is set to "true".
	 */
	@Test
	public void testMissingPathWithIgnoringUnknownPaths() {
		new Expectations() {
			{
				swaggerUnitConfiguration.getIgnoreUnknownPathCalls();
				result = true;
			}
		};
		URI toTest = URI.create("/v1/notexisting");
		swaggerUnitCore.validateRequest("GET", toTest, null, "{}");
	}

	/**
	 * Tests if an unknown path call will be ignored by swaggerunit, if the configuration key is set to "false", but the blacklist containts the path.
	 */
	@Test
	public void testMissingPathOnBlacklist() {
		new Expectations() {
			{
				swaggerUnitConfiguration.getIgnoreUnknownPathCalls();
				result = false;
				swaggerUnitConfiguration.getValidationPathIgnoreList();
				result = Collections.singletonList(".*");
			}
		};
		URI toTest = URI.create("/v1/notexisting");
		swaggerUnitCore.validateRequest("GET", toTest, null, "{}");
	}

	@Test
	public void testMissingBodyFields() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString("Object has missing required properties ([\"contractId\",\"tariffId\"])"));
		@SuppressWarnings("serial") Map<String, List<String>> headers = new HashMap<String, List<String>>() {
			{
				put("Channel", Collections.singletonList("App"));
				put("Agent", Collections.singletonList("anyone"));
			}
		};
		URI toTest = URI.create("/v1/contracts/tariffSwap");
		swaggerUnitCore.validateRequest("POST", toTest, headers, "{}");
	}

	@Test
	public void testErrorMessageGetsAdditionalInformationOnMissingHeaders() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString("Mandatory header \"Agent\" is not set."));
		URI toTest = URI.create("/v1/contracts/tariffSwap?contractId=mc.123324");
		Map<String, List<String>> headers = new HashMap<>();
		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

	@Test
	public void allHeaderMissing_shouldFail() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString(
				"Header parameter 'Agent' is required on path '/v1/contracts/tariffSwap' but not found in request."));
		thrown.expectMessage(containsString(
				"Header parameter 'Channel' is required on path '/v1/contracts/tariffSwap' but not found in request."));
		URI toTest = URI.create("/v1/contracts/tariffSwap?contractId=mc.123324");
		Map<String, List<String>> headers = new HashMap<>();
		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

	@Test
	public void queryParamIsMissing_shouldFail() {
		thrown.expect(SwaggerValidationException.class);
		thrown.expectMessage(containsString(
				"Query parameter 'contractId' is required on path '/v1/contracts/tariffSwap' but not found in request."));
		URI toTest = URI.create("/v1/contracts/tariffSwap");
		@SuppressWarnings("serial") Map<String, List<String>> headers = new HashMap<>() {
			{
				put("Channel", Collections.singletonList("App"));
				put("Agent", Collections.singletonList("Fox Mulder"));
			}
		};
		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

	@Test
	public void shouldIgnoreAdditionalHeaders_shouldPass() {
		URI toTest = URI.create("/v1/contracts/tariffSwap?contractId=mc.123324");

		@SuppressWarnings("serial") Map<String, List<String>> headers = new HashMap<String, List<String>>() {{
			put("Channel", Collections.singletonList("App"));
			put("Agent", Collections.singletonList("Fox Mulder"));
			put("Something", Collections.singletonList("Somewhere"));
		}};

		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

	@Test
	public void testValidation_withPathParams() {

		URI toTest = URI.create("/v1/contracts/reactivation/MC.12345/check");

		@SuppressWarnings("serial") Map<String, List<String>> headers = new HashMap<>() {{
			put("Channel", Collections.singletonList("App"));
			put("Agent", Collections.singletonList("Fox Mulder"));
			put("userInfo", Collections.singletonList("anyUserInfo"));
		}};

		swaggerUnitCore.validateRequest("GET", toTest, headers, null);
	}

}
