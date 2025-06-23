package com.example.oauth.member.controller;

import com.example.oauth.common.auth.JwtTokenProvider;
import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.GoogleProfileDto;
import com.example.oauth.member.dto.KakaoProfileDto;
import com.example.oauth.member.dto.MemberCreateRequest;
import com.example.oauth.member.dto.MemberLoginRequest;
import com.example.oauth.member.dto.NaverProfileDto;
import com.example.oauth.member.dto.RedirectDto;
import com.example.oauth.member.entity.Member;
import com.example.oauth.member.service.GoogleService;
import com.example.oauth.member.service.KakaoService;
import com.example.oauth.member.service.MemberService;
import com.example.oauth.member.service.NaverService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final JwtTokenProvider jwtTokenProvider;

	private final MemberService memberService;
	private final GoogleService googleService;
	private final KakaoService kakaoService;
	private final NaverService naverService;

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

	@PostMapping("/member/google/doLogin")
	public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto) {
		// 구글로 부터 .. access token 요청해서 발급 받음 . 파라미터 -> 프론트에서 받은 인가 코드 .
		AccessTokenDto accessTokenDto = googleService.getAccessToken(redirectDto.getCode());

		// 구글로 부터 토큰 값 받아서 토큰으로 사용자 정보 얻기 .
		GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(
			accessTokenDto.getAccess_token());
		// 회원가입이 되어 있지 않으면 회원 가입 진행, 회원가입 되어 있으면 ..access 토큰 발급.
		// member service 에서 member repository 찾아보고 .. socialId
		Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
		if (originalMember == null) {
			originalMember = memberService.createMemberWithGoogle(googleProfileDto);
		}

		String jwtToken = jwtTokenProvider.createToken(
			originalMember.getEmail(), originalMember.getRole());

		Map<String, Object> loginInfo = new HashMap<>();
		loginInfo.put("id", originalMember.getId());
		loginInfo.put("token", jwtToken);
		// 이렇게 jwt token 을 반환하면 .. 프론트엔드에서 localStorage 에 token 을 저장함 .
		return ResponseEntity.ok(loginInfo);
	}

	@PostMapping("/member/kakao/doLogin")
	public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDto redirectDto) {
		// access token 발급 받기 .
		AccessTokenDto accessTokenDto = kakaoService.getAccessToken(redirectDto.getCode());
		// 카카오에서 사용자 정보 받아오기 .
		KakaoProfileDto kakaoProfileDto =
			kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());
		// 회원가입 안되어있으면 회원 가입 후 토큰 반환, 되어있으면 바로 토큰 반환 .
		Member originMember = memberService.getMemberBySocialId(kakaoProfileDto.getId());
		if (originMember == null) {
			originMember = memberService.createMemberWithKakao(kakaoProfileDto);
		}

		String token = jwtTokenProvider
			.createToken(originMember.getEmail(), originMember.getRole());

		Map<String, Object> loginInfo = new HashMap<>();
		loginInfo.put("id", originMember.getId());
		loginInfo.put("token", token);

		return ResponseEntity.ok(loginInfo);
	}

	@PostMapping("/member/naver/doLogin")
	public ResponseEntity<?> naverLogin(@RequestBody RedirectDto redirectDto) {
		AccessTokenDto accessTokenDto = naverService
			.getAccessToken(redirectDto.getCode(), redirectDto.getState());

		// 네이버에서 사용자 정보 가져오기 ..
		NaverProfileDto naverProfileDto = naverService.getNaverProfile(
			accessTokenDto.getAccess_token());

		// 회원 가입 되었는지 확인후 .. 토큰 발급해줌
		Member originMember = memberService.getMemberBySocialId(
			naverProfileDto.getResponse().getEmail());
		if (originMember == null) {
			originMember = memberService.createMemberWithNaver(naverProfileDto);
		}

		String token = jwtTokenProvider.createToken(originMember.getEmail(),
			originMember.getRole());

		Map<String, Object> loginInfo = new HashMap<>();
		loginInfo.put("id", originMember.getId());
		loginInfo.put("token", token);

		return ResponseEntity.ok(loginInfo);
	}
}
