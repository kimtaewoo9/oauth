package com.example.oauth.common.auth;

import com.example.oauth.member.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private final String secretKey;

	@Value("${jwt.expiration}")
	private final int expiration;

	private Key SECRET_KEY;

	public JwtTokenProvider(
		@Value("${jwt.secret}") String secretKey,
		@Value("${jwt.expiration}") int expiration) {
		this.secretKey = secretKey;
		this.expiration = expiration;
		this.SECRET_KEY = new SecretKeySpec(
			java.util.Base64.getDecoder().decode(secretKey),
			SignatureAlgorithm.HS512.getJcaName()); // 사용할 암호화 알고리즘 .. SHA 512
	}

	public String createToken(String email, Role role) {
		// claims -> jwt token 의 payload 부분임 .
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("role", role.toString()); // Role type을 string 으로 전달

		Date now = new Date();

		// claims(email, role), issuedAt, Expiration, secret_key가 필요 .
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + expiration * 60 * 1000L)) // getTime() 은 밀리초 단위
			.signWith(SECRET_KEY)
			.compact();
	}
}
