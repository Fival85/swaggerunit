package de.md.swaggerunit.adapter.restassured;

import de.md.swaggerunit.adapter.RequestDto;
import de.md.swaggerunit.adapter.ResponseDto;
import de.md.swaggerunit.adapter.SwaggerUnitAdapter;
import de.md.swaggerunit.adapter.SwaggerUnitValidation;
import io.restassured.config.HttpClientConfig;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author mlipka
 * @since 25.11.2019
 */
public class SwaggerUnitRestAssuredAdapter implements HttpClientConfig.HttpClientFactory, SwaggerUnitAdapter {

	private AbstractHttpClient httpClient;
	private List<SwaggerUnitValidation> swaggerUnitValidationList = new ArrayList<>();

	public SwaggerUnitRestAssuredAdapter(AbstractHttpClient httpClient) {
		super();
		this.httpClient = httpClient;
	}

	@Override
	public HttpClient createHttpClient() {
		httpClient.addRequestInterceptor(this::validateRequestInterceptor);
		httpClient.addResponseInterceptor(this::validateResponseInterceptor);
		return httpClient;
	}

	/**
	 * Validate the incoming request.
	 *
	 * @param request -
	 * @throws IOException -
	 */
	private void validateRequestInterceptor(HttpRequest request, HttpContext httpContext) throws IOException {
		RequestDto requestInformation = new RequestDto();
		requestInformation.setMethod(request.getRequestLine().getMethod());
		requestInformation.setUri(URI.create(request.getRequestLine().getUri()));
		byte[] body = new byte[0];
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity reqEntity = ((HttpEntityEnclosingRequest) request).getEntity();
			if (reqEntity != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				reqEntity.writeTo(baos);
				body = baos.toByteArray();
			}
		}
		requestInformation.setBody(new String(body));
		Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Header header : request.getAllHeaders()) {
			headers.put(header.getName(), Collections.singletonList(header.getValue()));
		}
		requestInformation.setHeaders(headers);
		httpContext.setAttribute("requestInformation", requestInformation);
		httpContext.setAttribute("apacheHttpRequest", request);
	}

	/**
	 * Validate outgoing response.
	 *
	 * @param response -
	 * @param context  Context is needed to resend the same request as the response body needs to be evaluated
	 * @throws IOException -
	 */
	private void validateResponseInterceptor(HttpResponse response, HttpContext context) throws IOException {
		final RequestDto requestDto = (RequestDto) context.getAttribute("requestInformation");
		Map<String, List<String>> headers = new HashMap<>();
		for (Header header : response.getAllHeaders()) {
			headers.put(header.getName(), Collections.singletonList(header.getValue()));
		}
		final HttpRequest originalHttpRequest = (HttpRequest) context.getAttribute("apacheHttpRequest");
		// else an exception will be thrown "org.apache.http.ProtocolException: Content-Length header already present"
		HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		originalHttpRequest.removeHeaders(HTTP.CONTENT_LEN);
		//TODO this is an ugly solution and it will only work for non indempotent requests.
		final CloseableHttpResponse reproducedResponse = new DefaultHttpClient().execute(target, originalHttpRequest);
		ResponseDto responseDto = new ResponseDto(response.getStatusLine().getStatusCode(), headers,
				new String(reproducedResponse.getEntity().getContent().readAllBytes()));
		swaggerUnitValidationList.add(new SwaggerUnitValidation(requestDto, responseDto));
	}

	@Override
	public SwaggerUnitValidation[] getSwaggerUnitValidations() {
		return swaggerUnitValidationList.toArray(SwaggerUnitValidation[]::new);
	}

}
