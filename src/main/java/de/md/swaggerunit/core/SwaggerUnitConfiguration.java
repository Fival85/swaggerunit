// #***************************************************************************
// # mobilcom Vertrieb & Kunde Services - Source File: BaseTestConfiguration.java
// # Copyright (c) 1996-2018 by mobilcom-debitel GmbH
// # Author: mmalitz, Created on: 21.03.2018
// # All rights reserved.
// #***************************************************************************
package de.md.swaggerunit.core;

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
}
