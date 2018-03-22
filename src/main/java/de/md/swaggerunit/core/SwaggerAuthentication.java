// #***************************************************************************
// # mobilcom Vertrieb & Kunde Services - Source File: SwaggerAuthentication.java
// # Copyright (c) 1996-2018 by mobilcom-debitel GmbH
// # Author: mmalitz, Created on: 21.03.2018
// # All rights reserved.
// #***************************************************************************
package de.md.swaggerunit.core;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.swagger.models.auth.AuthorizationValue;

@Component
public class SwaggerAuthentication {

	private static final String LOGIN_FORM_FIELD_PASSWORD = "password"; //NOSONAR
	private static final String LOGIN_FORM_FIELD_USERNAME = "username";
	private static final String HEADER_NAME_AUTHORIZATION = "Authorization";

	private static final String AUTHORIZATION_VALUE_TYPE_HEADER = "header";
	
	private static Optional<String> authToken = Optional.empty();
	
	@Inject
	private RestTemplate restTemplate;
	
	@Inject
	private SwaggerUnitConfiguration swaggerUnitConfiguration;
	
	Optional<AuthorizationValue> getAuth() {
		return loginAndGetToken().map(this::tokenToAuthorizationValue);
	}

	private AuthorizationValue tokenToAuthorizationValue(String token) {
		final String authKey = HEADER_NAME_AUTHORIZATION;
		AuthorizationValue authorizationValue = new AuthorizationValue();
		authorizationValue.setKeyName(authKey);
		authorizationValue.setValue(token);
		authorizationValue.setType(AUTHORIZATION_VALUE_TYPE_HEADER);
		return authorizationValue;
	}

	private synchronized Optional<String> loginAndGetToken() {
		if (!authToken.isPresent()) {
			SwaggerAuthenticationResponse authResponse = authenticate();
			if (StringUtils.isNotBlank(authResponse.getToken())) {
				authToken = Optional.of("Bearer " + authResponse.getToken());
			}
		}
		return authToken;
	}

	private SwaggerAuthenticationResponse authenticate() {
		MultiValueMap<String, String> loginForm = createLoginForm();
		return restTemplate.postForObject(swaggerUnitConfiguration.getSwaggerLoginUrl(), loginForm, SwaggerAuthenticationResponse.class);
	}

	private MultiValueMap<String, String> createLoginForm() {
		MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
		loginForm.add(LOGIN_FORM_FIELD_USERNAME, swaggerUnitConfiguration.getSwaggerLoginUsername());
		loginForm.add(LOGIN_FORM_FIELD_PASSWORD, swaggerUnitConfiguration.getSwaggerLoginPassword());
		return loginForm;
	}

}
