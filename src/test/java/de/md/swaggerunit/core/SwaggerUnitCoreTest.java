package de.md.swaggerunit.core;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import de.md.swaggerunit.adapter.classic.SwaggerUnitClassicConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SwaggerUnitCoreTest {
	private final SwaggerUnitClassicConfiguration givenConfig = new SwaggerUnitClassicConfiguration("/simpleYaml.yml");
	private final SwaggerUnitCore coreToTest = new SwaggerUnitCore();

	@Before
	public void setUp() {
		coreToTest.setSwaggerUnitConfiguration(givenConfig);
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
}
