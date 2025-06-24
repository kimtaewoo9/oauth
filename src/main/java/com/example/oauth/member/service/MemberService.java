package com.example.oauth.member.service;

import com.example.oauth.member.dto.DiscordProfileDto;
import com.example.oauth.member.dto.GithubProfileDto;
import com.example.oauth.member.dto.GoogleProfileDto;
import com.example.oauth.member.dto.KakaoProfileDto;
import com.example.oauth.member.dto.MemberCreateRequest;
import com.example.oauth.member.dto.MemberLoginRequest;
import com.example.oauth.member.dto.NaverProfileDto;
import com.example.oauth.member.entity.Member;
import com.example.oauth.member.entity.SocialType;
import com.example.oauth.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member create(MemberCreateRequest memberCreateRequest) {

		String encodedPassword =
			passwordEncoder.encode(memberCreateRequest.getPassword());

		Member member = Member.builder()
			.email(memberCreateRequest.getEmail())
			.password(encodedPassword)
			.build();

		return memberRepository.save(member);
	}

	public Member login(MemberLoginRequest memberLoginRequest) {
		Optional<Member> optionalMember =
			memberRepository.findByEmail(memberLoginRequest.getEmail());

		if (!optionalMember.isPresent()) {
			throw new IllegalArgumentException("[MemberService.login] 이메일이 존재하지 않습니다.");
		}

		Member member = optionalMember.get();
		if (!passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword())) {
			throw new IllegalArgumentException("[MemberService.login] login failed");
		}

		return member;
	}

	public Member getMemberBySocialId(String socialId) {
		return memberRepository.findBySocialId(socialId).orElse(null);
	}

	@Transactional
	public Member createMemberWithGoogle(GoogleProfileDto googleProfileDto) {
		String name = googleProfileDto.getFamily_name() + googleProfileDto.getGiven_name();
		Member member = Member.builder()
			.email(googleProfileDto.getEmail())
			.socialType(SocialType.GOOGLE)
			.socialId(googleProfileDto.getSub())
			.name(name)
			.build();

		return memberRepository.save(member);
	}

	@Transactional
	public Member createMemberWithKakao(KakaoProfileDto kakaoProfileDto) {
		String name = kakaoProfileDto.getKakao_account().getProfile().getNickname();
		Member member = Member.builder()
			.email(kakaoProfileDto.getKakao_account().getEmail())
			.socialType(SocialType.KAKAO)
			.socialId(kakaoProfileDto.getId())
			.name(name)
			.build();

		return memberRepository.save(member);
	}

	@Transactional
	public Member createMemberWithNaver(NaverProfileDto naverProfileDto) {
		String name = naverProfileDto.getResponse().getName();
		String email = naverProfileDto.getResponse().getEmail();

		Member member = Member.builder()
			.email(email)
			.socialType(SocialType.NAVER)
			.socialId(naverProfileDto.getResponse().getId())
			.name(name)
			.build();

		return memberRepository.save(member);
	}

	@Transactional
	public Member createMemberWithGithub(GithubProfileDto githubProfileDto) {
		String email = githubProfileDto.getEmail();
		String name = githubProfileDto.getName();

		Member member = Member.builder()
			.name(name)
			.email(email)
			.socialType(SocialType.GITHUB)
			.socialId(githubProfileDto.getId())
			.build();

		return memberRepository.save(member);
	}

	public Member createMemberWithDiscord(DiscordProfileDto discordProfileDto) {
		String email = discordProfileDto.getEmail();
		String username = discordProfileDto.getUsername();

		Member member = Member.builder()
			.email(email)
			.name(username)
			.socialType(SocialType.DISCORD)
			.socialId(discordProfileDto.getId())
			.build();

		return memberRepository.save(member);
	}
}
