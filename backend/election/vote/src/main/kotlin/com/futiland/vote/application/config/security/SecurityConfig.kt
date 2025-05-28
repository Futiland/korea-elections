package com.futiland.vote.application.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:3000",
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
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
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


        // 권한 규칙 작성
        http.authorizeHttpRequests { httpRequest ->
            httpRequest.anyRequest().permitAll()
//            httpRequest.requestMatchers("/**").permitAll()
        }




        return http.build()
    }
}