package com.example.oauth.member.service;

import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.KakaoProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {

	private final RestClient restClient = RestClient.create();

	@Value("${oauth.kakao.client_id}")
	private String clientId;
	// 카카오는 client secret 이 없음.
	@Value("${oauth.kakao.redirect_uri}")
	private String redirectUri;

	private String grantType = "authorization_code";

	public AccessTokenDto getAccessToken(String code) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("code", code);
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", grantType);

		ResponseEntity<AccessTokenDto> response = restClient.post()
			.uri("https://kauth.kakao.com/oauth/token")
			.header("Content-Type", "application/x-www-form-urlencoded")
			.body(params)
			.retrieve()
			.toEntity(AccessTokenDto.class);

		return response.getBody();
	}

	public KakaoProfileDto getKakaoProfile(String token) {

		ResponseEntity<KakaoProfileDto> response = restClient.get()
			.uri("https://kapi.kakao.com/v2/user/me")
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.toEntity(KakaoProfileDto.class);
		
		return response.getBody();
	}
}
