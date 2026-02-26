package com.company.leave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String API_LEAVES_PATH = "/api/leaves/**";
	private static final String ROLE_ADMIN = "ADMIN";

	private final JwtFilter jwtFilter;

	@Bean
	SecurityFilterChain filter(HttpSecurity http) {

		http.csrf(csrf -> csrf.disable())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

						.requestMatchers(HttpMethod.POST, API_LEAVES_PATH).hasRole(ROLE_ADMIN)
						.requestMatchers(HttpMethod.PUT, API_LEAVES_PATH).hasRole(ROLE_ADMIN)
						.requestMatchers(HttpMethod.GET, API_LEAVES_PATH).hasAnyRole(ROLE_ADMIN, "USER")

						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}