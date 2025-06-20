package com.example.oauth.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberCreateRequest {

	private String email;
	private String password;
}
