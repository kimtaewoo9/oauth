package com.example.oauth.member.service;

import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.DiscordProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class DiscordService {

	private final RestClient restClient = RestClient.create();

	@Value("${oauth.discord.client_id}")
	private String clientId;

	@Value("${oauth.discord.client_secret}")
	private String clientSecret;

	@Value("${oauth.discord.redirect_uri}")
	private String redirectUri;

	private String grantType = "authorization_code";

	public AccessTokenDto getAccessToken(String code) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("grant_type", grantType);
		params.add("code", code);

		System.out.println("Client ID      : [" + clientId + "]");
		System.out.println("Client Secret      : [" + clientSecret + "]");
		System.out.println("Code      : [" + code + "]");
		System.out.println("Redirect URI   : [" + redirectUri + "]");
		System.out.println("Grant Type     : [" + grantType + "]");

		ResponseEntity<AccessTokenDto> response = restClient.post()
			.uri("https://discord.com/api/oauth2/token")
			.header("Content-Type", "application/x-www-form-urlencoded")
			.body(params)
			.retrieve()
			.toEntity(AccessTokenDto.class);

		return response.getBody();
	}

	public DiscordProfileDto getDiscordProfile(String token) {
		ResponseEntity<DiscordProfileDto> response = restClient.get()
			.uri("https://discord.com/api/users/@me")
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.toEntity(DiscordProfileDto.class);

		return response.getBody();
	}
}
