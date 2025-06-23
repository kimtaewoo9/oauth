package com.example.oauth.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleProfileDto {

	// 구글에서 전달 받을 profile 의 dto .
	private String sub; // ID 값은 주된 값이라는 의미인 sub로 들어옴
	private String email;
	private String given_name;
	private String family_name;
	private String picture; // 프로필 사진을 url 로 줌 ..
}
