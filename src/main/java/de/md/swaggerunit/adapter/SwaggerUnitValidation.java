package de.md.swaggerunit.adapter;

public class SwaggerUnitValidation {

	private RequestDto request;
	private ResponseDto response;

	public SwaggerUnitValidation(RequestDto request, ResponseDto response) {
		this.request = request;
		this.response = response;
	}

	private SwaggerUnitValidation(Builder builder) {
		this.request = builder.request;
		this.response = builder.response;
	}

	public static Builder newSwaggerUnitValidation() {
		return new Builder();
	}

	public RequestDto getRequest() {
		return request;
	}

	public void setRequest(RequestDto request) {
		this.request = request;
	}

	public ResponseDto getResponse() {
		return response;
	}

	public void setResponse(ResponseDto response) {
		this.response = response;
	}

	public static final class Builder {
		private RequestDto request;
		private ResponseDto response;

		private Builder() {
		}

		public SwaggerUnitValidation build() {
			return new SwaggerUnitValidation(this);
		}

		public Builder request(RequestDto request) {
			this.request = request;
			return this;
		}

		public Builder response(ResponseDto response) {
			this.response = response;
			return this;
		}
	}
}
