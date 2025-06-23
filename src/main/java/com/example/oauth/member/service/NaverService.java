package com.example.oauth.member.service;

import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.NaverProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class NaverService {

	private final RestClient restClient = RestClient.create();

	@Value("${oauth.naver.client_id}")
	private String clientId;

	@Value("${oauth.naver.client_secret}")
	private String clientSecret;

	private String grantType = "authorization_code";

	public AccessTokenDto getAccessToken(String code, String state) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("state", state);
		params.add("grant_type", grantType);

		ResponseEntity<AccessTokenDto> response = restClient.post()
			.uri("https://nid.naver.com/oauth2.0/token")
			.header("Content-Type", "application/x-www-form-urlencoded")
			.body(params)
			.retrieve()
			.toEntity(AccessTokenDto.class);
		log.info("✅ response.getBody().getAccessToken()={}", response.getBody().getAccess_token());

		return response.getBody();
	}

	public NaverProfileDto getNaverProfile(String token) {
		ResponseEntity<NaverProfileDto> response = restClient.get()
			.uri("https://openapi.naver.com/v1/nid/me")
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.toEntity(NaverProfileDto.class);

		log.info("✅ response.getBody() = " + response.getBody());

		return response.getBody();
	}
}
