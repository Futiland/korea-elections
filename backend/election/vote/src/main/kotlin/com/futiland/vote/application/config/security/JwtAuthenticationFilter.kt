package com.futiland.vote.application.config.security

import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.common.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    private val excludedPaths: List<RequestMatcher> = listOf(
        AntPathRequestMatcher("/", "GET"),
        AntPathRequestMatcher("/account/v1/signup", "POST"),
        AntPathRequestMatcher("/account/v1/signin", "POST"),
        AntPathRequestMatcher("/election/v1/*/vote", "GET"), // GET만 제외
        AntPathRequestMatcher("/swagger/**"),
        AntPathRequestMatcher("/swagger-ui/**"),
        AntPathRequestMatcher("/api-docs/**")
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
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
                    CustomUserDetails(CustomUserDto(
                        accountId= tokenResult.accountId,
                    ))

                if (userDetails != null) {
                    val usernamePasswordAuthenticationToken =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
