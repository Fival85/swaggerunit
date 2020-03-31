// #***************************************************************************
// # mobilcom Vertrieb & Kunde Services - Source File: BaseTestConfiguration.java
// # Copyright (c) 1996-2018 by mobilcom-debitel GmbH
// # Author: mmalitz, Created on: 21.03.2018
// # All rights reserved.
// #***************************************************************************
package de.md.swaggerunit.adapter.restassured;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.md.swaggerunit.core.SwaggerUnitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SwaggerUnitRestAssuredConfigurationImpl implements SwaggerUnitConfiguration {

	private String swaggerSourceOverride;
	private String swaggerLoginUrl;
	private String swaggerLoginUsername;
	private String swaggerLoginPassword;    //NOSONAR
	private String fallbackContentType;
	private Boolean ignoreUnknownPathCalls;
	private List<String> validationPathIgnoreList;
	private String swaggerSource;

	public SwaggerUnitRestAssuredConfigurationImpl() {
	}

	public SwaggerUnitRestAssuredConfigurationImpl(String swaggerSource) {
		this.swaggerSource = swaggerSource;
	}

	public SwaggerUnitRestAssuredConfigurationImpl(String swaggerSourceOverride, String swaggerLoginUrl,
			String swaggerLoginUsername, String swaggerLoginPassword) {
		this.swaggerSourceOverride = swaggerSourceOverride;
		this.swaggerLoginUrl = swaggerLoginUrl;
		this.swaggerLoginUsername = swaggerLoginUsername;
		this.swaggerLoginPassword = swaggerLoginPassword;
	}

	@Override
	public String getSwaggerSourceOverride() {
		return swaggerSourceOverride;
	}

	public void setSwaggerSourceOverride(String swaggerSourceOverride) {
		this.swaggerSourceOverride = swaggerSourceOverride;
	}

	@Override
	public String getSwaggerLoginUrl() {
		return swaggerLoginUrl;
	}

	public void setSwaggerLoginUrl(String swaggerLoginUrl) {
		this.swaggerLoginUrl = swaggerLoginUrl;
	}

	@Override
	public String getSwaggerLoginUsername() {
		return swaggerLoginUsername;
	}

	public void setSwaggerLoginUsername(String swaggerLoginUsername) {
		this.swaggerLoginUsername = swaggerLoginUsername;
	}

	@Override
	public String getSwaggerLoginPassword() {
		return swaggerLoginPassword;
	}

	public void setSwaggerLoginPassword(String swaggerLoginPassword) {
		this.swaggerLoginPassword = swaggerLoginPassword;
	}

	@Override
	public String getSwaggerSource() {
		return swaggerSource;
	}

	public void setSwaggerSource(String swaggerSource) {
		this.swaggerSource = swaggerSource;
	}

	@Override
	public String getFallbackContentType() {
		return fallbackContentType;
	}

	public void setFallbackContentType(String fallbackContentType) {
		this.fallbackContentType = fallbackContentType;
	}

	@Override
	public Boolean getIgnoreUnknownPathCalls() {
		return ignoreUnknownPathCalls;
	}

	public void setIgnoreUnknownPathCalls(Boolean ignoreUnknownPathCalls) {
		this.ignoreUnknownPathCalls = ignoreUnknownPathCalls;
	}

	public void setIgnoreUnknownPathCalls(boolean ignoreUnknownPathCalls) {
		this.ignoreUnknownPathCalls = ignoreUnknownPathCalls;
	}

	/**
	 * If {@link SwaggerUnitConfiguration#getIgnoreUnknownPathCalls()} is not set or set to <tt>true</tt>,
	 * all request calls will be validated. Even if this not exists in the swagger/openAPI definition.
	 * If you want to ignore a defined set of path, you could set here a list of regular expressions for the request.
	 * <p>
	 * This is a nice feature, if you want to have the surety that all necessary request will be validated.
	 * </p>
	 *
	 * @return a list of regular expressions of paths to be ignored while validation
	 */
	@Override
	public List<String> getValidationPathIgnoreList() {
		return validationPathIgnoreList;
	}

	public void setValidationPathIgnoreList(List<String> validationPathIgnoreList) {
		this.validationPathIgnoreList = validationPathIgnoreList;
	}

	@Bean
	public RestTemplate swaggerUnitHttpClient(@Autowired ObjectMapper objectMapper) {
		RestTemplate swaggerUnitHttpClient = new RestTemplate();
		swaggerUnitHttpClient.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			protected boolean hasError(HttpStatus statusCode) {
				return false;
			}
		});
		// Suche nach dem MessageConverter für JSON (Jackson) und setze dort den vorkonfigurierten ObjectMapper
		swaggerUnitHttpClient.getMessageConverters().forEach(httpMessageConverter -> {
			if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
				((MappingJackson2HttpMessageConverter) httpMessageConverter).setObjectMapper(objectMapper);
			}
		});
		return swaggerUnitHttpClient;
	}

}
