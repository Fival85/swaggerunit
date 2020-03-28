package de.md.swaggerunit.adapter.spring;

import de.md.swaggerunit.adapter.ClonedHttpResponse;
import de.md.swaggerunit.adapter.RequestDto;
import de.md.swaggerunit.adapter.ResponseDto;
import de.md.swaggerunit.adapter.SwaggerUnitAdapter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

@Component
public class SwaggerUnitSpringAdapter implements ClientHttpRequestInterceptor, SwaggerUnitAdapter {

	private RequestDto requestDto;
	private ResponseDto responseDto;

	public SwaggerUnitSpringAdapter(RestTemplate swaggerUnitHttpClient) {
		super();
		swaggerUnitHttpClient.setInterceptors(Arrays.asList(this));
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		requestDto = new RequestDto(request.getMethod().name(), request.getURI(), request.getHeaders(), new String(body));
		ClientHttpResponse response = execution.execute(request, body);
		ClonedHttpResponse clonedHttpResponse = ClonedHttpResponse.createFrom(response);
		responseDto = new ResponseDto(response.getRawStatusCode(), request.getHeaders(),
				new String(clonedHttpResponse.getRawBody()));
		return response;
	}

	@Override
	public RequestDto getRequest() {
		return requestDto;
	}

	@Override
	public ResponseDto getResponse() {
		return responseDto;
	}

}
