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

	private ResponseDto(Builder builder) {
		this.statusCode = builder.statusCode;
		this.headers = builder.headers;
		this.body = builder.body;
	}

	public static Builder newResponseDto() {
		return new Builder();
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

	public static final class Builder {
		private int statusCode;
		private Map<String, List<String>> headers;
		private String body;

		private Builder() {
		}

		public ResponseDto build() {
			return new ResponseDto(this);
		}

		public Builder statusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public Builder headers(Map<String, List<String>> headers) {
			this.headers = headers;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}
	}
}
