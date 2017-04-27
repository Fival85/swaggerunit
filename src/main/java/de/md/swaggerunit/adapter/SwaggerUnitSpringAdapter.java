package de.md.swaggerunit.adapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import de.md.swaggerunit.core.SwaggerUnitCore;
import de.md.swaggerunit.usage.ValidationScope;

public class SwaggerUnitSpringAdapter implements ClientHttpRequestInterceptor {

	private SwaggerUnitCore unitCore;

	private ValidationScope validationScope = ValidationScope.NONE;

	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUnitSpringAdapter.class);

	public SwaggerUnitSpringAdapter(SwaggerUnitCore unitCore) {
		super();
		this.unitCore = unitCore;
	}

	public void setValidationScope(ValidationScope validationScope) {
		Objects.requireNonNull(validationScope, "please provide a non null validation scope.");
		this.validationScope = validationScope;
	}

	/**
	 * Test if the response body of a response is formatted as json. This function doesn't
	 * actually inspect the body, if just checks if the content-type header contains
	 * something like "application/json".
	 *
	 * @param response
	 * @return true if the response body is formatted as json.
	 */
	private boolean isJsonResponse(ClientHttpResponse response){
		return response
			.getHeaders()
			.getContentType()
			.isCompatibleWith(MediaType.APPLICATION_JSON);
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		if (validationScope == ValidationScope.REQUEST || validationScope == ValidationScope.BOTH) {
			unitCore.validateRequest(request.getMethod().name(), request.getURI(), request.getHeaders(), new String(body));
		}
		ClientHttpResponse response = execution.execute(request, body);
		if (isJsonResponse(response) && (validationScope == ValidationScope.RESPONSE|| validationScope == ValidationScope.BOTH)) {
			ClonedHttpResponse clonedHttpResponse = ClonedHttpResponse.createFrom(response);
			//TODO: only do this if the content type is json!
			//TODO: dont use the default charset to create the body as string.
			unitCore.validateResponse(request.getMethod().name(), response.getRawStatusCode(), request.getURI(), request.getHeaders(), new String(clonedHttpResponse.getRawBody()));
			return clonedHttpResponse;
		}
		return response;
	}

}