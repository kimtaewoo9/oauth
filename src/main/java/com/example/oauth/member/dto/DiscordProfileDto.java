package com.example.oauth.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordProfileDto {

	private String id;
	private String username;
	private String avatar;
	private String email;
}
