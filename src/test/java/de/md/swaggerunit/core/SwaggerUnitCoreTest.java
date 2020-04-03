package de.md.swaggerunit.core;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import de.md.swaggerunit.adapter.classic.SwaggerUnitClassicConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SwaggerUnitCoreTest {
	private final SwaggerUnitClassicConfiguration givenConfig = new SwaggerUnitClassicConfiguration("/simpleYaml.yml");
	private final SwaggerUnitCore coreToTest = new SwaggerUnitCore();

	@Before
	public void setUp() {
		coreToTest.setConfig(givenConfig);
	}

	@Test
	public void testTryToLoadSwaggerFromFileAsResource() {
		final OpenAPI swaggerFile = coreToTest.tryToLoadSwaggerFromFile();
		assertThat(swaggerFile.getPaths().get("/test/simpleString"), is(notNullValue()));
	}

	@Test
	public void testInitValidator() {
		coreToTest.initValidator();
		final OpenApiInteractionValidator validator = coreToTest.getValidator();
		assertNotNull(validator);
	}

	@Test
	public void testCollectQueryParams() {
		URI givenUri = URI.create("/test?arr=2&arr=3");
		final Map<String, List<String>> queryParams = coreToTest.getQueryParams(givenUri);
		assertThat(queryParams.containsKey("arr"), is(true));
		assertThat(queryParams.get("arr"), is(List.of("2", "3")));
	}

	@Test
	public void testCollectQueryParamsWithCommaSeparetedList() {
		URI givenUri = URI.create("/test?arr=2,2testCollectQueryParamsWithCommaSeparatedList4&arr=3");
		final Map<String, List<String>> queryParams = coreToTest.getQueryParams(givenUri);
		assertThat(queryParams.containsKey("arr"), is(true));
		assertThat(queryParams.get("arr"), is(List.of("2", "2", "4", "3")));
	}
}
