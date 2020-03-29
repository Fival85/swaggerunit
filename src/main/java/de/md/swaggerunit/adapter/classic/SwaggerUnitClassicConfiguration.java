package de.md.swaggerunit.adapter.classic;

import de.md.swaggerunit.core.SwaggerUnitConfiguration;

public class SwaggerUnitClassicConfiguration implements SwaggerUnitConfiguration {

	private String swaggerSourceOverride;

	private String swaggerLoginUrl;

	private String swaggerLoginUsername;

	private String swaggerLoginPassword;    //NOSONAR

	private String swaggerSource;
	private String fallbackContentType;

	public SwaggerUnitClassicConfiguration() {
	}

	/**
	 * Constructs the configuration with only the source of the swagger.
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
	 * @param swaggerSource
	 * @return the swagger source of the swagger
	 */
	public SwaggerUnitClassicConfiguration(String swaggerSource) {
		this.swaggerSource = swaggerSource;
	}

	@Override
	public String getSwaggerSourceOverride() {
		return swaggerSourceOverride;
	}

	/**
	 * The used OpenApi-Validator does not validate request without a content-type header. If the response do not have
	 * anyone we could manipulate the content-type header. With this value you could override the default fallback value "application/json".
	 *
	 * @param swaggerSourceOverride the wished fallback content-type header value
	 */
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

	/**
	 * Sets the source of the swagger.
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
	 * @param swaggerSource
	 * @return the swagger source of the swagger
	 */
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
}
