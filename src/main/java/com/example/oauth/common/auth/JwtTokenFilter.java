package com.example.oauth.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.security.sasl.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenFilter extends GenericFilter {

	@Value("${jwt.secret}")
	private String secretKey;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		// 서버는 토큰을 검증해야함 . 이 filter 에서 토큰을 검증한다 .
		// Authorization 헤더에서 .. 토큰을 꺼내줌 . (클라이언트가 보낸 토큰)
		String token = httpServletRequest.getHeader("Authorization");

		try {
			// 토큰이 있는 경우에만 token을 검증 .
			if (token != null) {
				// 클라이언트가 보낼때는 .. 앞에 Bearer 를 붙이고 보내라는 약속이 있기 때문에 그 부분 substring 해야함 .
				if (!token.substring(0, 7).equals("Bearer ")) {
					throw new AuthenticationException("[JwtTokenFilter.doFilter] Bearer 형식이 아닙니다.");
				}

				String jwtToken = token.substring(0, 7);
				Claims claims = Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(jwtToken)
					.getBody(); // claims 에 담겨있는 정보 사용하기 위해 claims 추출 .

				// 권한 생성 @PreAuthorize("hasRole('ADMIN')") 과 같은 애노테이션을 사용하려면 ROLE_ 를 붙여야함 .
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));

				// UserDetail 생성 사용자 이름, 비밀번호, 권한 등 사용자 정보 등 .. 필요한 핵심 정보를 담는 표준 규격 .
				UserDetails userDetails = new User(claims.getSubject(), "", authorities);

				// Authentication 객체 생성하고 Security Context Holder 에 저장 .
				// UsernamePasswordAuthentication 이라는 Authentication 인터페이스의 구현체를 사용 .
				// 사용자 정보 객체, jwtToken(보통 password 를 넣음), 권한 세가지를 넣음 .
				Authentication authentication = new UsernamePasswordAuthenticationToken(
					userDetails, jwtToken, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType("application/json");
			httpServletResponse.getWriter().write("invalid token");
			// chain 으로 돌아가지 않음 . 서버 로직으로 들어가지 않고 사용자에게 바로 응답 반환 .
		}
	}
}
