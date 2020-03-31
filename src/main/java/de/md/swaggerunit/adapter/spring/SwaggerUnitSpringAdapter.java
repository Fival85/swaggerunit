package de.md.swaggerunit.adapter.spring;

import de.md.swaggerunit.adapter.ClonedHttpResponse;
import de.md.swaggerunit.adapter.RequestDto;
import de.md.swaggerunit.adapter.ResponseDto;
import de.md.swaggerunit.adapter.SwaggerUnitAdapter;
import de.md.swaggerunit.adapter.SwaggerUnitValidation;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SwaggerUnitSpringAdapter implements ClientHttpRequestInterceptor, SwaggerUnitAdapter {

	private List<SwaggerUnitValidation> swaggerUnitValidationList = new ArrayList<>();

	public SwaggerUnitSpringAdapter(RestTemplate swaggerUnitHttpClient) {
		super();
		swaggerUnitHttpClient.setInterceptors(Arrays.asList(this));
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		RequestDto requestDto = new RequestDto(request.getMethod().name(), request.getURI(), request.getHeaders(),
				new String(body));
		ClientHttpResponse response = execution.execute(request, body);
		ClonedHttpResponse clonedHttpResponse = ClonedHttpResponse.createFrom(response);
		ResponseDto responseDto = new ResponseDto(response.getRawStatusCode(), request.getHeaders(),
				new String(clonedHttpResponse.getRawBody()));
		swaggerUnitValidationList.add(new SwaggerUnitValidation(requestDto, responseDto));
		return response;
	}

	@Override
	public SwaggerUnitValidation[] getSwaggerUnitValidations() {
		final SwaggerUnitValidation[] swaggerUnitValidations = swaggerUnitValidationList.toArray(SwaggerUnitValidation[]::new);
		swaggerUnitValidationList.clear();
		return swaggerUnitValidations;
	}
}
