package de.md.swaggerunit.core;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.interaction.ApiOperationResolver;
import com.atlassian.oai.validator.model.ApiOperationMatch;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Request.Method;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.md.swaggerunit.adapter.RequestDto;
import de.md.swaggerunit.adapter.ResponseDto;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwaggerUnitCore {

	public static final String SKIP_VALIDATION_VALUE = "true";
	public static final String SKIP_VALIDATION_KEY = "swaggerunit.validation.skip";
	public static final String FALLBACK_CONTENT_TYPE_HEADER_VALUE = "application/json";
	private static final String STRICT_VALIDATION_KEY = "swaggerunit.validation.strict";
	private static final String STRICT_VALIDATION_VALUE = "true";
	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUnitCore.class);

	private SwaggerAuthentication authentication;

	private SwaggerUnitConfiguration swaggerUnitConfiguration;

	private OpenApiInteractionValidator validator;

	private OpenAPI openAPI;

	/**
	 * Constructor without automatically initialization for unit-test purposes.
	 */
	SwaggerUnitCore() {
	}

	public SwaggerUnitCore(SwaggerUnitConfiguration swaggerUnitConfiguration) {
		this.swaggerUnitConfiguration = swaggerUnitConfiguration;
		this.authentication = new SwaggerAuthentication(new RestTemplate(), swaggerUnitConfiguration);
		init();
	}

	@Inject
	public SwaggerUnitCore(SwaggerUnitConfiguration swaggerUnitConfiguration, SwaggerAuthentication authentication) {
		this.swaggerUnitConfiguration = swaggerUnitConfiguration;
		this.authentication = authentication;
		init();
	}

	/**
	 * Initialized the SwaggerUnitCore.
	 * Exception from the initialization process will be hided until the system property swaggerunit.validation.strict is set to "true"
	 */
	private void init() {
		try {
			initSwagger();
			initValidator();
		} catch (Exception ex) {
			if (ex instanceof RestClientException) {
				LOGGER.error("Exception for http call to {}", swaggerUnitConfiguration.getSwaggerLoginUrl());
			} else {
				LOGGER.error(
						"Swagger from '" + swaggerUnitConfiguration.getSwaggerSourceOverride() + "' couldn't be initialized",
						ex);
			}
			if (STRICT_VALIDATION_VALUE.equalsIgnoreCase(System.getProperty(STRICT_VALIDATION_KEY))) {
				throw ex;
			}
			// skips the validation per default, until the SwaggerValidation annotation is used in a test
			System.setProperty(SKIP_VALIDATION_KEY, SKIP_VALIDATION_VALUE);
		}
	}

	private void initSwagger() {
		openAPI = tryToLoadSwaggerFromHttp();
		if (openAPI == null) {
			openAPI = tryToLoadSwaggerFromFile();
		}
		if (openAPI == null) {
			throw new RuntimeException("No swagger could be found. please set a swagger file or a http uri to the swagger");
		}
	}

	private String getSwaggerSource() {
		final String swaggerSourceOverride = swaggerUnitConfiguration.getSwaggerSourceOverride();
		return (swaggerSourceOverride == null || swaggerSourceOverride.isBlank()) ?
				swaggerUnitConfiguration.getSwaggerSource() :
				swaggerSourceOverride;
	}

	OpenAPI tryToLoadSwaggerFromFile() {
		String swaggerSource = getSwaggerSource();
		if (swaggerSource == null) {
			return null;
		}
		return new OpenAPIV3Parser().read(swaggerSource);
	}

	OpenAPI tryToLoadSwaggerFromHttp() {
		String swaggerSourceUrl = getSwaggerSource();
		if (swaggerSourceUrl == null || swaggerSourceUrl.isBlank()) {
			return null;
		}
		if (!isUrl(swaggerSourceUrl)) {
			return null;
		}
		return new OpenAPIV3Parser()
				.readLocation(swaggerSourceUrl, authentication.getAuth().map(Arrays::asList).orElse(null), new ParseOptions())
				.getOpenAPI();
	}

	void initValidator() {
		String swaggerSource = getSwaggerSource();
		final OpenApiInteractionValidator.Builder builder = OpenApiInteractionValidator.createFor(swaggerSource);
		validator = builder.build();
	}

	/**
	 * Simple function to test, if a string is a valid representation of an URL or not.
	 *
	 * @param str - the string to check
	 * @return true is the string is a valid URL
	 */
	private boolean isUrl(String str) {
		try {
			new URL(str);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public void validateRequest(RequestDto requestDto) {
		validateRequest(requestDto.getMethod(), requestDto.getUri(), requestDto.getHeaders(), requestDto.getBody());
	}

	/**
	 * Validiert den Request gegen die YAML.
	 *
	 * @param method  -
	 * @param uri     -
	 * @param headers -
	 * @param body    -
	 */
	public void validateRequest(String method, URI uri, Map<String, List<String>> headers, String body) {
		Method requestMethod = Method.valueOf(method);
		SimpleRequest.Builder requestBuilder = new SimpleRequest.Builder(requestMethod, uri.getPath()).withBody(body);
		if (headers != null) {
			headers.forEach(requestBuilder::withHeader);
		}
		// the used validator from Atlassian does not validate if no content-type is set, so we set a fallback
		if (headers == null || !headers.containsKey("content-type")) {
			requestBuilder.withHeader("content-type", getFallbackContentTypeHeaderValue());
		}
		String rawQuery = uri.getQuery();
		Map<String, List<String>> parsedQueryParams = new HashMap<>();
		if (rawQuery != null && !rawQuery.isEmpty()) {
			String[] queryParams = rawQuery.split("&");
			for (String queryParam : queryParams) {
				String[] split = queryParam.split("=");
				if (split.length == 2) {
					String key = split[0];
					String value = split[1];
					if (parsedQueryParams.containsKey(key)) {
						parsedQueryParams.get(key).add(value);
					} else {
						List<String> values = new ArrayList<>();
						values.add(value);
						parsedQueryParams.put(key, values);
					}
				}
			}
		}
		parsedQueryParams.forEach(requestBuilder::withQueryParam);
		SimpleRequest simpleRequest = requestBuilder.build();
		ValidationReport validationReport = validator.validateRequest(simpleRequest);

		ApiOperationMatch apiOperation = getApiOperation(openAPI, uri.getPath(), Method.valueOf(method));
		if (apiOperation.isPathFound() && apiOperation.isOperationAllowed()) {
			Collection<Message> validationHeaderMessages = apiOperation.getApiOperation().getOperation().getParameters()
					.stream()
					.filter(param -> "header".equalsIgnoreCase(param.getIn()) && param.getRequired() && (headers == null
							|| !headers.containsKey(param.getName()))).map(param -> new HeaderMessage(param.getName(),
							String.format("Mandatory header \"%s\" is not set.", param.getName())))
					.collect(Collectors.toList());
			ValidationReport validationHeaderReport = ValidationReport.from(validationHeaderMessages);

			ValidationReport mergedValidationReport = validationReport.merge(validationHeaderReport);
			processValidationReport(mergedValidationReport);
		} else {
			LOGGER.info("Request für URI: {} wurde nicht validiert.", uri);
		}
	}

	private void processValidationReport(ValidationReport validationReport) {
		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Validierungsergebnis SwaggerUnit: {}", new ObjectMapper().writeValueAsString(validationReport));
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Das Validierungsergebnis von SwaggerUnit konnte nicht ausgegeben werden.", e);
		}
		if (validationReport != null && validationReport.hasErrors()) {
			String message = validationReport.getMessages().stream()
					//TODO: filter by parameter?
					.map(Message::getMessage).collect(Collectors.joining(" "));
			if (!message.isEmpty()) {
				throw new SwaggerValidationException(message);
			}
		}
	}

	public ApiOperationMatch getApiOperation(OpenAPI openAPI, String path, Request.Method method) {
		return new ApiOperationResolver(openAPI, null).findApiOperation(path, method);
	}

	public void validateResponse(RequestDto requestDto, ResponseDto responseDto) {
		validateResponse(requestDto.getMethod(), responseDto.getStatusCode(), requestDto.getUri(), responseDto.getHeaders(),
				responseDto.getBody());
	}

	public void validateResponse(String method, int statusCode, URI uri, Map<String, List<String>> headers, String body) {
		SimpleResponse.Builder responseBuilder = new SimpleResponse.Builder(statusCode).withBody(body);
		if (headers != null) {
			headers.forEach((k, v) -> responseBuilder.withHeader(k, v.toArray(new String[0])));
		}
		// the used validator from Atlassian does not validate if no content-type is set, so we set a fallback
		if (headers == null || !headers.containsKey("content-type")) {
			responseBuilder.withHeader("content-type", getFallbackContentTypeHeaderValue());
		}

		ApiOperationMatch apiOperation = getApiOperation(openAPI, uri.getPath(), Method.valueOf(method));
		// Only validate if path exists in swagger
		if (apiOperation.isPathFound() && apiOperation.isOperationAllowed()) {
			SimpleResponse response = responseBuilder.build();
			ValidationReport validationReport = validator.validateResponse(uri.getPath(), Method.valueOf(method), response);
			processValidationReport(validationReport);
		} else {
			LOGGER.info("Response für URI: {} wurde nicht validiert.", uri);
		}
	}

	private String getFallbackContentTypeHeaderValue() {
		final String wishedFallbackContentType = swaggerUnitConfiguration.getFallbackContentType();
		return wishedFallbackContentType == null || wishedFallbackContentType.isBlank() ?
				FALLBACK_CONTENT_TYPE_HEADER_VALUE :
				wishedFallbackContentType;
	}

	/**
	 * Only for unit testing purposes
	 *
	 * @param swaggerUnitConfiguration
	 */
	void setSwaggerUnitConfiguration(SwaggerUnitConfiguration swaggerUnitConfiguration) {
		this.swaggerUnitConfiguration = swaggerUnitConfiguration;
	}

	/**
	 * Only for unit testing purposes
	 *
	 * @return
	 */
	OpenApiInteractionValidator getValidator() {
		return validator;
	}
}
