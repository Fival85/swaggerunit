package de.md.swaggerunit.adapter.spring.base;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("junit-test")
public abstract class BaseTest {

	protected static final HttpHeaders DEFAULT_HEADERS = createDefaultHeaders();
	// Lokaler MicroService
	private static final String LOCAL_SERVICE_HOST = "localhost";
	private static final Integer LOCAL_SERVICE_PORT = 8100;
	protected static final String LOCAL_SERVICE_BASE_URL = "http://" + LOCAL_SERVICE_HOST + ":" + LOCAL_SERVICE_PORT + "/";

	@Qualifier("swaggerUnitHttpClient")
	@Autowired
	protected RestTemplate client;

	protected static String createUri(String relativeUrl) {
		return UriComponentsBuilder.fromUriString(LOCAL_SERVICE_BASE_URL).path(relativeUrl).build().toUriString();
	}

	protected static HttpHeaders createDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Agent", "JUNIT");
		headers.add("Channel", "PRIVATESERVICE");
		headers.add("userInfo",
				"{\"customerReferences\":[\"MC.19387089\"],\"customerRoles\":{\"productOwner\":[\"MC.220413004\", \"contractIdExistiertNicht\"]}}");
		headers.add("X-Organization", "MD");
		return headers;
	}

}
