package com.futiland.vote.application.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.common.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
    private val environment: Environment
) : OncePerRequestFilter() {

    // NOTE: 인증 필요 여부는 SecurityConfig의 authorizeHttpRequests에서만 관리
    // 이 필터는 Authorization 헤더가 있으면 항상 검증하고, 없으면 통과
    // SecurityConfig에서 authenticated() 규칙이 있으면 403 에러 발생

    // 공개 엔드포인트 목록 (JWT 검증 스킵)
    // GET 요청들도 포함 (브라우저/프론트가 Authorization 헤더를 실수로 보낼 수 있음)
    private val publicEndpoints = listOf(
        // 기본 경로
        AntPathRequestMatcher("/"),

        // Account 관련
        AntPathRequestMatcher("/account/v1/signup"),
        AntPathRequestMatcher("/account/v1/signin"),
        AntPathRequestMatcher("/account/v1/change-password"),
        AntPathRequestMatcher("/account/v1/stopper"),

        // Actuator
        AntPathRequestMatcher("/actuator/health"),

        // Swagger
        AntPathRequestMatcher("/swagger-ui.html"),
        AntPathRequestMatcher("/swagger-ui/**"),
        AntPathRequestMatcher("/v3/api-docs/**"),
        AntPathRequestMatcher("/swagger-resources/**"),

        // Election - GET
        AntPathRequestMatcher("/election/v1/*/vote", "GET"),

        // Poll - GET (모든 조회 API)
        AntPathRequestMatcher("/poll/v1/public", "GET")
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 공개 엔드포인트는 JWT 검증 스킵
        if (publicEndpoints.any { it.matches(request) }) {
            filterChain.doFilter(request, response)
            return
        }

        val authorizationHeader = request.getHeader("Authorization")

        // Authorization 헤더가 있으면 검증, 없으면 그냥 통과
        // SecurityConfig에서 authenticated() 규칙이 있으면 인증 없을 때 403 처리됨
        if (!authorizationHeader.isNullOrBlank() && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)

            if (jwtTokenProvider.validateToken(token)) {
                val tokenResult: AccountJwtPayload = jwtTokenProvider.parseAuthorizationToken(token)

                val userDetails: UserDetails =
                    CustomUserDetails(
                        CustomUserDto(
                            accountId = tokenResult.accountId,
                        )
                    )

                val usernamePasswordAuthenticationToken =
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            } else {
                // 토큰이 유효하지 않은 경우
                response.contentType = "application/json"
                response.status = HttpStatus.UNAUTHORIZED.value()
                val errorResponse = HttpApiResponse.fromExceptionMessage(
                    code = CodeEnum.FRS_002,
                    message = "유효하지 않은 토큰입니다. 재로그인 해주세요."
                )
                objectMapper.writeValue(response.outputStream, errorResponse)
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
