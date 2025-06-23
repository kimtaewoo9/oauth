package com.example.oauth.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubProfileDto {

	private String login;
	private String id; // social id
	private String email;
	private String name;
	private String avatar_url;
}
