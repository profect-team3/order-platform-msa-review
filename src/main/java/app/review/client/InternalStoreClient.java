package app.review.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import app.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InternalStoreClient {

	private final RestTemplate restTemplate;

	@Value("${store.service.url:http://localhost:8082}")
	private String storeServiceUrl;

	public ApiResponse<String> getStoreName(UUID storeId) {
		String url = storeServiceUrl + "/internal/store/" + storeId + "/name";

		ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<ApiResponse<String>>() {}
		);

		return response.getBody();
	}

}
