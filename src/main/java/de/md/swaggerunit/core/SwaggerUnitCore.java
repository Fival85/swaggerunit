package de.md.swaggerunit.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.oai.validator.SwaggerRequestResponseValidator;
import com.atlassian.oai.validator.model.Request.Method;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.MutableValidationReport;
import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.SwaggerDeserializationResult;

public class SwaggerUnitCore {

	protected static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUnitCore.class);
	private SwaggerRequestResponseValidator validator;
	private Swagger swagger;

	public SwaggerUnitCore(String swaggerUri) {
		validator = SwaggerRequestResponseValidator.createFor(swaggerUri).build();
		SwaggerDeserializationResult swaggerDeserializationResult = new SwaggerParser().readWithInfo(swaggerUri, null, true);
		swagger = swaggerDeserializationResult.getSwagger();
	}

	/**
	 * Validiert den Request gegen die YAML.
	 * @param method
	 * @param uri
	 * @param header
	 * @param body
	 * @throws JsonProcessingException 
	 */
	public void validateRequest(String method, URI uri, Map<String, List<String>> header, String body)  {
		String basePath = swagger.getBasePath();
		Method requestMethod = Method.valueOf(method);
		String relUri = uri.getPath().substring(basePath.length(), uri.getPath().length());
		SimpleRequest.Builder requestBuilder = new SimpleRequest.Builder(requestMethod, relUri).withBody(body);
		if (header != null) {
			header.forEach((k, v) -> {
				requestBuilder.withHeader(k, v);
			});
		}
		String rawQuery = uri.getQuery();
		Map<String, List<String>> parsedQueryParams = new HashMap<>();
		if(rawQuery != null && !rawQuery.isEmpty()) {
			String[] queryParams = rawQuery.split("&");
			for(String queryParam : queryParams) {
				String[] split = queryParam.split("=");
				if(split.length == 2) {
					String key = split[0];
					String value = split[1];
					if(parsedQueryParams.containsKey(key)) {
						parsedQueryParams.get(key).add(value);
					} else {
						List<String> values = new ArrayList<>();
						values.add(value);
						parsedQueryParams.put(key, values);
					}
				} else {
					// log warn/info
				}
			}
		}
		parsedQueryParams.forEach( (k,v) -> {
			requestBuilder.withQueryParam(k, v);
		});
		SimpleRequest simpleRequest = requestBuilder.build();
    	ValidationReport validationReport = validator.validateRequest(simpleRequest);

		String pathWithoutBasePath = uri.getPath().substring(basePath.length());
		Path path = swagger.getPath(pathWithoutBasePath);
		if (path == null) {
			throw new SwaggerValidationException(String.format("unable to find path for \"%s\".", pathWithoutBasePath));
		}


		MutableValidationReport fuckThis = new MutableValidationReport();
		fuckThis.addAll(validationReport);

		getOperationForMethodFromPath(path, requestMethod)
			.getParameters()
			.stream()
			.filter(param -> "header".equalsIgnoreCase(param.getIn()) && param.getRequired() && (header == null || !header.containsKey(param.getName())))
			.map(param -> new HeaderMessage(param.getName(), String.format("Mandatory header \"%s\" is not set.", param.getName())))
			.forEach(fuckThis::add);
		processValidationReport(fuckThis);
	}

	private void processValidationReport(ValidationReport validationReport) {
		try {
			LOGGER.info("Validierungsergebniss SwaggerUnit: {}", new ObjectMapper().writeValueAsString(validationReport));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(validationReport != null && validationReport.hasErrors()){
			String message = validationReport.getMessages().stream()
					//TODO: filter by parameter?
					.map(m -> m.getMessage())
					.collect(Collectors.joining(" "));
			if(message != null && !message.isEmpty()) {
				throw new SwaggerValidationException(message);
			}
		}
	}

	private Operation getOperationForMethodFromPath(Path path, Method method){
		switch(method){
			case DELETE:
				return path.getDelete();
			case GET:
				return path.getGet();
			case PATCH:
				return path.getPatch();
			case POST:
				return path.getPost();
			case PUT:
				return path.getPut();
			default:
				//just in case one day TRACE or so will be added to the Method enum ^^.
				throw new UnsupportedOperationException(String.format("http verb \"%s\" is not supported.", method));
		}
	}

	public void validateResponse(String method, int statusCode, URI uri, Map<String, List<String>> headers, String body) {
		SimpleResponse.Builder responseBuilder = new SimpleResponse.Builder(statusCode).withBody(body);
		if(headers != null){
			headers.forEach((k, v) -> {
				responseBuilder.withHeader(k, v.toArray(new String[v.size()]));
			});
		}
		String basePath = swagger.getBasePath();
		String relUri = uri.getPath().substring(basePath.length());
		SimpleResponse response = responseBuilder.build();
		ValidationReport validationReport = validator.validateResponse(relUri, Method.valueOf(method), response);
		processValidationReport(validationReport);
	}
}