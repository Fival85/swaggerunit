package de.md.swaggerunit.core;

import com.atlassian.oai.validator.SwaggerRequestResponseValidator;
import de.md.swaggerunit.adapter.classic.SwaggerUnitClassicConfiguration;
import io.swagger.models.Swagger;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
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
		final Swagger swaggerFile = coreToTest.tryToLoadSwaggerFromFile();
		assertThat(swaggerFile.getBasePath(), equalTo("/test"));
	}

	@Test
	public void testInitValidator() {
		coreToTest.initValidator();
		final SwaggerRequestResponseValidator validator = coreToTest.getValidator();
		assertNotNull(validator);
	}
}
