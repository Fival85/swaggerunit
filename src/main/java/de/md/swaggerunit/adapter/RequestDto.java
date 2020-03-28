package de.md.swaggerunit.adapter;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class RequestDto {
	private String method;
	private URI uri;
	private Map<String, List<String>> headers;
	private String body;

	public RequestDto() {
	}

	public RequestDto(String method, URI uri, String body) {
		this.method = method;
		this.uri = uri;
		this.body = body;
	}

	public RequestDto(String method, URI uri, Map<String, List<String>> headers, String body) {
		this.method = method;
		this.uri = uri;
		this.headers = headers;
		this.body = body;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
