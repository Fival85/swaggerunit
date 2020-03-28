package de.md.swaggerunit.adapter.restassured;

import de.md.swaggerunit.adapter.RequestDto;
import de.md.swaggerunit.adapter.ResponseDto;
import de.md.swaggerunit.adapter.SwaggerUnitAdapter;
import io.restassured.config.HttpClientConfig;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;
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
	private HttpRequest request;
	private RequestDto requestDto;
	private ResponseDto responseDto;

	public SwaggerUnitRestAssuredAdapter(AbstractHttpClient httpClient) {
		super();
		this.httpClient = httpClient;
	}

	@Override
	public HttpClient createHttpClient() {
		httpClient.addRequestInterceptor((request, context) -> validateRequestInterceptor(request));
		httpClient.addResponseInterceptor(this::validateResponseInterceptor);
		return httpClient;
	}

	/**
	 * Test if the response body of a response is formatted as json. This function doesn't actually inspect the body, if just
	 * checks if the content-type header contains something like "application/json".
	 *
	 * @param response -
	 * @return true if the response body is formatted as json.
	 */
	private boolean isJsonResponse(HttpResponse response) {
		return response.getFirstHeader("content-type") != null && response.getFirstHeader("content-type").getValue()
				.contains("application/json");
	}

	/**
	 * Validate the incoming request.
	 *
	 * @param request -
	 * @throws IOException -
	 */
	private void validateRequestInterceptor(HttpRequest request) throws IOException {
		this.request = request;
		this.requestDto = new RequestDto();
		requestDto.setMethod(request.getRequestLine().getMethod());
		requestDto.setUri(URI.create(request.getRequestLine().getUri()));
		byte[] body = new byte[0];
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity reqEntity = ((HttpEntityEnclosingRequest) request).getEntity();
			if (reqEntity != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				reqEntity.writeTo(baos);
				body = baos.toByteArray();
			}
		}
		requestDto.setBody(new String(body));
		Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Header header : request.getAllHeaders()) {
			headers.put(header.getName(), Collections.singletonList(header.getValue()));
		}
		requestDto.setHeaders(headers);
	}

	/**
	 * Validate outgoing response.
	 *
	 * @param response -
	 * @param context  Context is needed to resend the same request as the response body needs to be evaluated
	 * @throws IOException -
	 */
	private void validateResponseInterceptor(HttpResponse response, HttpContext context) throws IOException {

		HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		request.removeHeaders(HTTP.CONTENT_LEN);
		HttpEntity resEntity = new DefaultHttpClient().execute(target, request).getEntity();
		byte[] body = new byte[0];
		if (resEntity != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resEntity.writeTo(baos);
			body = baos.toByteArray();
		}
		Map<String, List<String>> headers = new HashMap<>();
		for (Header header : response.getAllHeaders()) {
			headers.put(header.getName(), Collections.singletonList(header.getValue()));
		}
		responseDto = new ResponseDto(response.getStatusLine().getStatusCode(), headers, new String(body));
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
