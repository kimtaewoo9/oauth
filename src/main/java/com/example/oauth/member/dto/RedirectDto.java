package com.example.oauth.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RedirectDto {

	private String code; // 프론트에서 인가 코드를 받아서 토큰 발급 전용 엔드포인트로 전송 .
	private String state; // naver 는 state 를 받아야함 ..
}
