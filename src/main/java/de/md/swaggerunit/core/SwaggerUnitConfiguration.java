// #***************************************************************************
// # mobilcom Vertrieb & Kunde Services - Source File: BaseTestConfiguration.java
// # Copyright (c) 1996-2018 by mobilcom-debitel GmbH
// # Author: mmalitz, Created on: 21.03.2018
// # All rights reserved.
// #***************************************************************************
package de.md.swaggerunit.core;

import java.util.List;

public interface SwaggerUnitConfiguration {

	/**
	 * @return
	 * @deprecated please use {@link SwaggerUnitConfiguration#getSwaggerSource()}
	 */
	@Deprecated
	String getSwaggerSourceOverride();

	String getSwaggerLoginUrl();

	String getSwaggerLoginUsername();

	String getSwaggerLoginPassword();

	/**
	 * Implement this method to set the source of the swagger.
	 * The URL can be an absolute HTTP/HTTPS URL, a File URL or a classpath location (without the classpath: scheme).
	 * <p>
	 * For example:
	 * <pre>
	 *  // Create from a publicly hosted HTTP location
	 *  "http://api.myservice.com/swagger.json"
	 *  // Create from a file on the local filesystem
	 *  "file://Users/myuser/tmp/swagger.json"
	 *  // Create from a classpath resource in the /api package
	 *  "/api/swagger.json"
	 *  // Create from a swagger JSON payload
	 *  "{\"swagger\": \"2.0\", ...}"
	 *  </pre>
	 *
	 * @return the swagger source of the swagger
	 */
	String getSwaggerSource();

	/**
	 * The used OpenApi-Validator does not validate request without a content-type header. If the response do not have
	 * anyone we could manipulate the content-type header. With this value you could override the default fallback value "application/json".
	 *
	 * @return the wished fallback content-type header value
	 */
	String getFallbackContentType();

	/**
	 * Per default, all requested uris will be validated. Set this value to true, to validate only in the swagger/openAPI defined paths. Default is 'false'
	 *
	 * @return boolean
	 */
	Boolean getIgnoreUnknownPathCalls();

	/**
	 * If {@link SwaggerUnitConfiguration#getIgnoreUnknownPathCalls()} is not set or set to <tt>true</tt>,
	 * all request calls will be validated. Even if this not exists in the swagger/openAPI definition.
	 * If you want to ignore a defined set of paths, you could set here a list of regular expressions for the request.
	 * <p>
	 * This is a nice feature, if you want to have the surety that all necessary request will be validated and known issues manuel be ignored.
	 * </p>
	 *
	 * @return a list of regular expressions of paths to be ignored while validation
	 */
	List<String> getValidationPathIgnoreList();
}
