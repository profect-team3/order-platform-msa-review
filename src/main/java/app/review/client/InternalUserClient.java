package app.review.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import app.global.apiPayload.ApiResponse;
import app.review.model.dto.response.GetUserInfoResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InternalUserClient {
	private final RestTemplate restTemplate;

	@Value("${store.service.url:http://localhost:8081}")
	private String userServiceUrl;

	public ApiResponse<GetUserInfoResponse> getUserInfo() {
		String url = userServiceUrl + "/internal/user/info" ;

		ResponseEntity<ApiResponse<GetUserInfoResponse>> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<ApiResponse<GetUserInfoResponse>>() {}
		);

		return response.getBody();
	}
}
