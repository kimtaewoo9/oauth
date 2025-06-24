package com.example.oauth.common.config;

import com.example.oauth.common.auth.JwtTokenFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenFilter jwtTokenFilter;

	@Bean
	public PasswordEncoder makePassword() {
		// 암호 해싱하기 .
		// 실제 저장된 해시값: {bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	// login, cors 관련 설정 적용
	@Bean
	public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
			// cors 설정을 어떻게 커스터마이징 할지에 대한 설명서를 함수 형태로 받음 .
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 rest api는 csrf에 비교적 안전함 .
			.httpBasic(AbstractHttpConfigurer::disable)
			// session 방식을 비활성화 하겠다 .. 세션 방식 vs 토큰 방식 .. 세션은 로그인 인증 값을 서버에서 저장함 .
			// 토큰 기반 무상 인증 방식을 구현할때 반드시 필요한 설정 (세션을 사용하지 말라)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 특정 url 패턴에 대해서는 인증처리 하지 않음 .
			// 나머지 url 패턴에 대해서는 토큰이 없거나 유효하지 않아 Authentication 객체가 없으면 이 단계에서 차단됨 .
			.authorizeHttpRequests(
				a -> a.requestMatchers("/member/create", "/member/doLogin",
						"/member/google/doLogin", "/member/kakao/doLogin",
						"/member/naver/doLogin", "/member/github/doLogin",
						"/member/discord/doLogin")
					.permitAll().anyRequest().authenticated())
			// 특정 url 을 제외하고 다 검증하겠다 .. 근데 어디서 ? jwtTokenFilter 에서 .
			// UsernamePasswordAuthenticationFilter 라는 스프링 기본 필터 앞에 jwtTokenFilter 배치함 .
			// 클라이언트가 JWT 토큰과 함께 요청을 보냄 .
			// jwtTokenFilter 가 token 을 검증하고 Authentication 객체를 생성한 후 SecurityContextHolder 에 저장함 .
			.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	// 설정 정의
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// 나중에 배포 url 같은거 여기에 넣어야함 .
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("*")); // 모든 요청 허용 .
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true); // Authorization header 를 사용할 수 있도록 만듦.

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// 이 애플리케이션이 처리할 수 있는 모든 URL 에 대해서 CORS 설정 적용 .
		// 어떤 api 를 호출하더라도 동일한 cors 정책 에서의 요청 허용함 .
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
