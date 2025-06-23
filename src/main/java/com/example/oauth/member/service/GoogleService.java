package com.example.oauth.member.service;

import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.GoogleProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class GoogleService {

	// 백엔드 서버는 전달 받은 인가 코드와 함께, 사전에 등록하여 발급 받은 클라이언트 ID와 클라이언트 secret을
	// 인증 서버의 토큰 발급 전용 엔드포인트로 전송 ..

	private final RestClient restClient = RestClient.create();

	private String code; // 인가 코드

	@Value("${oauth.google.client-id}")
	private String clientId;

	@Value("${oauth.google.client-secret}")
	private String clientSecret;

	private String grantType = "authorization_code";

	@Value("${oauth.google.redirect-uri}")
	private String redirectUri;

	// access token 발급 받기 .
	public AccessTokenDto getAccessToken(String code) {
		// 인가 코드, client_id, client_secret, redirect_uri, grant_type

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("grant_type", grantType);
		params.add("redirect_uri", redirectUri);

		ResponseEntity<AccessTokenDto> response = restClient.post()
			.uri("https://oauth2.googleapis.com/token")
			.header("Content-Type", "application/x-www-form-urlencoded")
			.body(params)
			.retrieve() // 응답 body 값만을 추출 ..
			.toEntity(AccessTokenDto.class);
		log.info("✅ response.getBody().getAccessToken() = {}",
			response.getBody().getAccess_token());

		return response.getBody();
	}

	// 사용자 정보 얻기 access 토큰만 전달하면 언제든지 profile 을 제공해줌 .
	public GoogleProfileDto getGoogleProfile(String token) {
		ResponseEntity<GoogleProfileDto> response = restClient.get()
			.uri("https://openidconnect.googleapis.com/v1/userinfo")
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.toEntity(GoogleProfileDto.class);

		System.out.println("✅ GoogleProfileDto: " + response.getBody());

		return response.getBody();
	}
}
