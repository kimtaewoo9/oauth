package com.example.oauth.member.controller;

import com.example.oauth.common.auth.JwtTokenProvider;
import com.example.oauth.member.dto.MemberCreateRequest;
import com.example.oauth.member.dto.MemberLoginRequest;
import com.example.oauth.member.entity.Member;
import com.example.oauth.member.service.MemberService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/member/create")
	public ResponseEntity<?> create(@RequestBody MemberCreateRequest memberCreateRequest) {
		Member member = memberService.create(memberCreateRequest);

		return ResponseEntity.ok(member.getId());
	}

	// login 을 하면 client 에서는 최종적으로 토큰을 받아야함 .
	@PostMapping("/member/doLogin")
	public ResponseEntity<?> doLogin(@RequestBody MemberLoginRequest memberLoginRequest) {
		// email, password 가 일치하는지 검증 .
		Member member = memberService.login(memberLoginRequest);

		// 일치할 경우 jwt access token 을 생성함 .
		String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole());

		HashMap<Object, Object> loginInfo = new HashMap<>();

		loginInfo.put("id", member.getId());
		loginInfo.put("token", jwtToken);

		return ResponseEntity.ok(loginInfo);
	}
}
