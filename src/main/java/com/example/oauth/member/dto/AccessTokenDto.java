package com.example.oauth.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenDto {

	// getAccessToken 을 할때 . 구글에서 여러가지 정보를 줌. 그걸 DTO 로 받는다 .
	private String access_token;
	private String expires_in;
	private String scope;
	private String token_type;
	private String id_token;
}
