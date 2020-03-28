package de.md.swaggerunit.core;

import com.atlassian.oai.validator.SwaggerRequestResponseValidator;
import com.atlassian.oai.validator.SwaggerRequestResponseValidator.Builder;
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
import de.md.swaggerunit.adapter.classic.SwaggerUnitClassicConfiguration;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SwaggerUnitCore {

	public static final String SKIP_VALIDATION_VALUE = "true";
	public static final String SKIP_VALIDATION_KEY = "swaggerunit.validation.skip";
	private static final String STRICT_VALIDATION_KEY = "swaggerunit.validation.strict";
	private static final String STRICT_VALIDATION_VALUE = "true";

	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUnitCore.class);

	private SwaggerAuthentication authentication;

	private SwaggerUnitConfiguration swaggerUnitConfiguration;

	private SwaggerRequestResponseValidator validator;

	private Swagger swagger;

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
		swagger = tryToLoadSwaggerDirectly();
		if (swagger == null) {
			swagger = tryToLoadSwaggerFromHttp();
		}
		if (swagger == null) {
			swagger = tryToLoadSwaggerFromFile();
		}
		if (swagger == null) {
			throw new RuntimeException("No swagger could be found. please set a swagger file or a http uri to the swagger");
		}
		initSwaggerBasePath();
	}

	private String getSwaggerSource() {
		final String swaggerSourceOverride = swaggerUnitConfiguration.getSwaggerSourceOverride();
		return (swaggerSourceOverride == null || swaggerSourceOverride.isBlank()) ?
				swaggerUnitConfiguration.getSwaggerSource() :
				swaggerSourceOverride;
	}

	Swagger tryToLoadSwaggerDirectly() {
		try {
			return new SwaggerParser().readWithInfo(getSwaggerSource()).getSwagger();
		} catch (Exception e) {
			return null;
		}
	}

	Swagger tryToLoadSwaggerFromFile() {
		String swaggerSource = getSwaggerSource();
		if (swaggerSource == null) {
			return null;
		}
		Path swaggerPath;
		if (swaggerSource.startsWith("file::/")) {
			swaggerPath = Paths.get(swaggerSource);
		} else {
			// load the swagger as resource
			try {
				swaggerSource = swaggerSource.startsWith("/") ? swaggerSource.substring(1) : swaggerSource;
				URL resource = ClassLoader.getSystemResource(swaggerSource);
				if (resource == null) {
					throw new RuntimeException("The resource file '" + swaggerSource + "' could be found");
				}
				File swaggerFile = new File(resource.toURI());
				swaggerPath = swaggerFile.toPath();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		StringBuilder swaggerContentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(swaggerPath)) {
			stream.forEach(s -> swaggerContentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			LOGGER.error("Swagger file could not be read: " + swaggerSource, e);
			throw new RuntimeException("Swagger file could not be read", e);
		}
		return new SwaggerParser().readWithInfo(swaggerContentBuilder.toString()).getSwagger();
	}

	Swagger tryToLoadSwaggerFromHttp() {
		String swaggerSourceUrl = getSwaggerSource();
		if (swaggerSourceUrl == null || swaggerSourceUrl.isBlank()) {
			return null;
		}
		if (!isUrl(swaggerSourceUrl)) {
			return null;
		}
		return new SwaggerParser()
				.readWithInfo(swaggerSourceUrl, authentication.getAuth().map(Arrays::asList).orElse(null), true).getSwagger();
	}

	void initSwaggerBasePath() {
		if (swagger.getBasePath() == null) {
			swagger.setBasePath("");
		}
	}

	void initValidator() {
		String swaggerSource = getSwaggerSource();
		Builder builder = SwaggerRequestResponseValidator.createFor(swaggerSource);
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

		ApiOperationMatch apiOperation = getApiOperation(swagger, uri.getPath(), Method.valueOf(method));
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

	public ApiOperationMatch getApiOperation(Swagger swagger, String path, Request.Method method) {
		return new ApiOperationResolver(swagger, null).findApiOperation(path, method);
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

		ApiOperationMatch apiOperation = getApiOperation(swagger, uri.getPath(), Method.valueOf(method));
		// Only validate if path exists in swagger
		if (apiOperation.isPathFound() && apiOperation.isOperationAllowed()) {
			SimpleResponse response = responseBuilder.build();
			ValidationReport validationReport = validator.validateResponse(uri.getPath(), Method.valueOf(method), response);
			processValidationReport(validationReport);
		} else {
			LOGGER.info("Response für URI: {} wurde nicht validiert.", uri);
		}
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
	SwaggerRequestResponseValidator getValidator() {
		return validator;
	}
}
