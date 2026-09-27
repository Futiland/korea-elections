package com.futiland.vote.application.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:3000",
            "https://korea-election.com",
            "*.korea-election.com",
            "*.vercel.app"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
//        configuration.allowedHeaders = listOf("Content-Type", "Authorization", "X-Requested-With")
        configuration.allowedHeaders = listOf(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )

//        configuration.allowedHeaders = listOf("*")


        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity, environment: Environment): SecurityFilterChain {
        //CSRF, CORS
        http.csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
        http.cors(Customizer.withDefaults())

        http.sessionManagement { sessionManagement: SessionManagementConfigurer<HttpSecurity?> ->
            sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
            )
        }

        //FormLogin, BasicHttp 비활성화
        http.formLogin { form: FormLoginConfigurer<HttpSecurity> -> form.disable() }
        http.httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }


        // JWT 인증 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        // 권한 규칙 작성
        // NOTE: 인증 필요 여부는 여기서만 관리 (JwtAuthenticationFilter와 중복 없음)
        http.authorizeHttpRequests { httpRequest ->
            // CORS preflight 요청 허용
            httpRequest.requestMatchers("OPTIONS", "/**").permitAll()

            // 기본 경로
            httpRequest.requestMatchers("/").permitAll()

            // Actuator health check (헬스체크용)
            httpRequest.requestMatchers("/actuator/health").permitAll()

            // Account 관련 - 인증 불필요
            httpRequest.requestMatchers("/account/v1/stopper").permitAll()
            httpRequest.requestMatchers("/account/v1/change-password").permitAll()
            httpRequest.requestMatchers("/account/v1/signup").permitAll()
            httpRequest.requestMatchers("/account/v1/signin").permitAll()
            httpRequest.requestMatchers("/account/v1/oauth/**").permitAll()  // OAuth 로그인

            // Election - GET만 인증 불필요
            httpRequest.requestMatchers("GET", "/election/v1/*/vote").permitAll()

            // Poll - 내 여론조사 조회는 인증 필요 (더 구체적인 규칙을 먼저 선언)
            httpRequest.requestMatchers("GET", "/poll/v1/my").authenticated()
            // Poll - 내가 참여한 투표 목록은 인증 필요
            httpRequest.requestMatchers("GET", "/poll/v1/public/response/my").authenticated()
            httpRequest.requestMatchers("GET", "/poll/v1/system/response/my").authenticated()

            // Poll Response - 비로그인 투표 허용 (서비스에서 검증)
            httpRequest.requestMatchers("POST", "/poll/v1/*/response").permitAll()
            httpRequest.requestMatchers("PUT", "/poll/v1/*/response").permitAll()
            httpRequest.requestMatchers("DELETE", "/poll/v1/*/response").permitAll()

            // Poll Result 조회 - 비로그인도 허용 (서비스에서 참여 여부 검증)
            httpRequest.requestMatchers("GET", "/poll/v1/*/result").permitAll()

            // Poll 조회 - GET만 인증 불필요
            httpRequest.requestMatchers("GET", "/poll/v1/**").permitAll()

            // Swagger (dev 프로파일일 때만)
            val activeProfiles = environment.activeProfiles
            if (activeProfiles.contains("dev") || activeProfiles.isEmpty()) {
                httpRequest.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
            }

            // 나머지는 모두 인증 필요
            // - POST/PUT/DELETE /poll/** (응답 제출/수정/삭제 등)
            // - GET /poll/v1/my (내 여론조사 조회)
            // - 기타 모든 인증이 필요한 엔드포인트
            httpRequest.anyRequest().authenticated()
        }

        return http.build()
    }
}