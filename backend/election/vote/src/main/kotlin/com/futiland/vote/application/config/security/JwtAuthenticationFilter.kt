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

    private val excludedPaths: List<RequestMatcher> by lazy {
        val basePaths = mutableListOf(
            AntPathRequestMatcher("/", "GET"),
            AntPathRequestMatcher("/account/v1/stopper", "GET"),
            AntPathRequestMatcher("/account/v1/change-password", "POST"),
            AntPathRequestMatcher("/account/v1/signup", "POST"),
            AntPathRequestMatcher("/account/v1/signin", "POST"),
            AntPathRequestMatcher("/election/v1/*/vote", "GET"), // GET만 제외
            AntPathRequestMatcher("/poll/v1/**", "GET") // Poll 조회만 인증 제외
        )

        // dev 프로파일일 때만 Swagger 경로 제외
        val activeProfiles = environment.activeProfiles
        if (activeProfiles.contains("dev") || activeProfiles.isEmpty()) {
            basePaths.addAll(
                listOf(
                    AntPathRequestMatcher("/swagger-ui.html"),
                    AntPathRequestMatcher("/swagger-ui/**"),
                    AntPathRequestMatcher("/v3/api-docs"),
                    AntPathRequestMatcher("/v3/api-docs/**"),
                    AntPathRequestMatcher("/swagger-resources/**"),
                )
            )
        }

        basePaths
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // /poll/v1/my는 인증 필요 (필터 적용)
        if (request.requestURI == "/poll/v1/my" && request.method == "GET") {
            return false
        }

        return excludedPaths.any { it.matches(request) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader.isNullOrBlank() || !authorizationHeader.startsWith("Bearer ")) {
            throw BadCredentialsException("Invalid Authorization header")
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)
            if (jwtTokenProvider.validateToken(token)) {
                val tokenResult: AccountJwtPayload = jwtTokenProvider.parseAuthorizationToken(token)

                val userDetails: UserDetails =
                    CustomUserDetails(
                        CustomUserDto(
                            accountId = tokenResult.accountId,
                        )
                    )

                if (userDetails != null) {
                    val usernamePasswordAuthenticationToken =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
                }
            } else{
                // 여기서 바로 예외 응답
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
