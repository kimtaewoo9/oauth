package com.example.oauth.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.aot.generate.GeneratedTypeReference;
import org.springframework.context.annotation.Configuration;

@Table(name = "member")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Role role = Role.USER; // admin, user

	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	private String socialId;
}
