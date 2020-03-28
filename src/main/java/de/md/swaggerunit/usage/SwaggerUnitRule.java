package de.md.swaggerunit.usage;

import de.md.swaggerunit.adapter.RequestDto;
import de.md.swaggerunit.adapter.SwaggerUnitAdapter;
import de.md.swaggerunit.core.SwaggerUnitConfiguration;
import de.md.swaggerunit.core.SwaggerUnitCore;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static de.md.swaggerunit.core.SwaggerUnitCore.SKIP_VALIDATION_KEY;
import static de.md.swaggerunit.core.SwaggerUnitCore.SKIP_VALIDATION_VALUE;

@Component
public class SwaggerUnitRule implements MethodRule {

	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUnitRule.class);
	private SwaggerUnitAdapter adapter;
	private SwaggerUnitCore unitCore;

	public SwaggerUnitRule(SwaggerUnitAdapter adapter, SwaggerUnitConfiguration config) {
		super();
		this.adapter = adapter;
		this.unitCore = new SwaggerUnitCore(config);
	}

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} catch (Throwable e) {
					LOGGER.error("the unit test throws an exception.", e);
					throw e;
				}
				SwaggerValidation annotation = method.getAnnotation(SwaggerValidation.class);
				if (shouldRunSwaggerValidation(annotation)) {
					// get request from adapter  and validate
					final RequestDto request = adapter.getRequest();
					if (shouldValidateRequest(annotation)) {
						unitCore.validateRequest(request);
					}
					if (shouldValidateResponse(annotation)) {
						unitCore.validateResponse(request, adapter.getResponse());
					}
				}
			}
		};
	}

	boolean shouldValidateResponse(SwaggerValidation annotation) {
		return ValidationScope.BOTH.equals(annotation.value()) || ValidationScope.RESPONSE.equals(annotation.value());
	}

	boolean shouldValidateRequest(SwaggerValidation annotation) {
		return ValidationScope.BOTH.equals(annotation.value()) || ValidationScope.REQUEST.equals(annotation.value());
	}

	boolean shouldRunSwaggerValidation(SwaggerValidation annotation) {
		return annotation != null && !ValidationScope.NONE.equals(annotation.value()) && !SKIP_VALIDATION_VALUE
				.equalsIgnoreCase(System.getProperty(SKIP_VALIDATION_KEY));
	}
}
