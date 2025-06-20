package com.example.oauth.member.service;

import com.example.oauth.member.dto.MemberCreateRequest;
import com.example.oauth.member.dto.MemberLoginRequest;
import com.example.oauth.member.entity.Member;
import com.example.oauth.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	private final PasswordEncoder passwordEncoder;

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
}
