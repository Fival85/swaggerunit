package de.md.swaggerunit.adapter.classic;

import de.md.swaggerunit.core.SwaggerUnitConfiguration;

import java.util.List;

public class SwaggerUnitClassicConfiguration implements SwaggerUnitConfiguration {

	private String swaggerSourceOverride;

	private String swaggerLoginUrl;

	private String swaggerLoginUsername;

	private String swaggerLoginPassword;    //NOSONAR

	private String swaggerSource;
	private String fallbackContentType;
	private Boolean ignoreUnknownPathCalls;
	private List<String> validationPathIgnoreList;

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

	@Override
	public Boolean getIgnoreUnknownPathCalls() {
		return ignoreUnknownPathCalls;
	}

	public void setIgnoreUnknownPathCalls(Boolean ignoreUnknownPathCalls) {
		this.ignoreUnknownPathCalls = ignoreUnknownPathCalls;
	}

	/**
	 * If {@link SwaggerUnitConfiguration#getIgnoreUnknownPathCalls()} is not set or set to <tt>true</tt>,
	 * all request calls will be validated. Even if this not exists in the swagger/openAPI definition.
	 * If you want to ignore a defined set of path, you could set here a list of regular expressions.
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
}
