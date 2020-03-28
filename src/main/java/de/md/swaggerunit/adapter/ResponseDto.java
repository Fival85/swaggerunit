package de.md.swaggerunit.adapter;

import java.util.List;
import java.util.Map;

public class ResponseDto {

	private int statusCode;
	private Map<String, List<String>> headers;
	private String body;

	public ResponseDto() {
	}

	public ResponseDto(int statusCode, String body) {
		this.statusCode = statusCode;
		this.body = body;
	}

	public ResponseDto(int statusCode, Map<String, List<String>> headers, String body) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
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
