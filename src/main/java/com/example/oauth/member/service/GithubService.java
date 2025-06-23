package com.example.oauth.member.service;

import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.GithubProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class GithubService {

	private final RestClient restClient = RestClient.create();

	@Value("${oauth.github.client_id}")
	private String clientId;

	@Value("${oauth.github.client_secret}")
	private String clientSecret;

	@Value("${oauth.github.redirect_uri}")
	private String redirectUri;

	public AccessTokenDto getAccessToken(String code, String state) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("redirect_uri", redirectUri);

		ResponseEntity<AccessTokenDto> response = restClient.post()
			.uri("https://github.com/login/oauth/access_token")
			.header("Content-Type", "application/x-www-form-urlencoded")
			.accept(MediaType.APPLICATION_JSON)
			.body(params)
			.retrieve()
			.toEntity(AccessTokenDto.class);

		log.info("access token: " + response.getBody());

		return response.getBody();
	}

	public GithubProfileDto getGithubProfile(String token) {
		ResponseEntity<GithubProfileDto> response = restClient.get()
			.uri("https://api.github.com/user")
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.toEntity(GithubProfileDto.class);

		log.info("response.getBody()={}", response.getBody());

		return response.getBody();
	}
}
