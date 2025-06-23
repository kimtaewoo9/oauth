package com.example.oauth.member.dto;

import lombok.Data;

@Data
public class NaverProfileDto {

	private String resultcode;
	private String message;
	private Response response;

	@Data
	public static class Response {

		private String id;
		private String nickname;
		private String email;
		private String name;
	}
}
