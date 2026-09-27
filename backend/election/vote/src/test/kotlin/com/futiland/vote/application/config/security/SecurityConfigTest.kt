package com.futiland.vote.application.config.security

import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.common.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecurityProbeController {
    @RequestMapping("/**")
    fun probe(): String = "ok"
}

abstract class SecurityConfigTestBase {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @MockitoBean
    protected lateinit var jwtTokenProvider: JwtTokenProvider

    protected fun perform(method: String, path: String, authorization: String? = null) =
        mockMvc.perform(
            request(HttpMethod.valueOf(method), path).apply {
                if (authorization != null) header("Authorization", authorization)
            }
        )
}

@WebMvcTest(controllers = [SecurityProbeController::class])
@Import(SecurityConfig::class, JwtAuthenticationFilter::class)
@ActiveProfiles("prod")
class SecurityConfigTest : SecurityConfigTestBase() {

    @ParameterizedTest(name = "비로그인 {0} {1} -> 허용")
    @CsvSource(
        "GET, /",
        "GET, /actuator/health",
        "POST, /account/v1/signup",
        "POST, /account/v1/signin",
        "POST, /account/v1/change-password",
        "GET, /account/v1/stopper",
        "GET, /account/v1/oauth/kakao/login",
        "GET, /account/v1/oauth/kakao/callback",
        "GET, /election/v1",
        "GET, /election/v1/1/vote",
        "GET, /election/v1/1/vote/result",
        "GET, /election/v1/1/vote/results",
        "GET, /election/v1/1/vote/results2",
        "GET, /poll/v1/public",
        "GET, /poll/v1/system",
        "GET, /poll/v1/detail/1",
        "GET, /poll/v1/1/result",
        "POST, /poll/v1/1/response",
        "PUT, /poll/v1/1/response",
        "DELETE, /poll/v1/1/response",
    )
    fun `공개 엔드포인트는 비로그인으로 접근할 수 있다`(method: String, path: String) {
        perform(method, path).andExpect(status().isOk)
    }

    @ParameterizedTest(name = "비로그인 {0} {1} -> 403")
    @CsvSource(
        "GET, /poll/v1/my",
        "GET, /poll/v1/public/response/my",
        "GET, /poll/v1/system/response/my",
        "POST, /poll/v1/public",
        "POST, /poll/v1/public/draft",
        "POST, /poll/v1/system",
        "PUT, /poll/v1/1",
        "DELETE, /poll/v1/1",
        "POST, /poll/v1/1/cancel",
        "POST, /election/v1/1/vote",
        "GET, /election/v1/1/vote/mine",
        "POST, /election/v1",
        "DELETE, /election/v1",
        "POST, /election/v1/candidate",
        "DELETE, /election/v1/candidate",
        "POST, /account/v1/batch/encrypt-all-accounts",
        "GET, /account/v1/info/profile",
        "GET, /account/v1/info/stats",
        "DELETE, /account/v1/me",
    )
    fun `인증 필요 엔드포인트는 비로그인이면 403`(method: String, path: String) {
        perform(method, path).andExpect(status().isForbidden)
    }

    @Test
    fun `프론트가 토큰 없이 보내는 Authorization None 헤더는 비로그인으로 취급한다`() {
        perform("GET", "/poll/v1/public", authorization = "None").andExpect(status().isOk)
        perform("GET", "/poll/v1/my", authorization = "None").andExpect(status().isForbidden)
    }

    @Test
    fun `CORS preflight는 인증 없이 허용된다`() {
        mockMvc.perform(
            options("/poll/v1/my")
                .header("Origin", "https://korea-election.com")
                .header("Access-Control-Request-Method", "GET")
        ).andExpect(status().isOk)
    }

    @Test
    fun `prod 프로파일에서는 Swagger가 차단된다`() {
        perform("GET", "/v3/api-docs").andExpect(status().isForbidden)
        perform("GET", "/swagger-ui/index.html").andExpect(status().isForbidden)
    }

    @Nested
    inner class 로그인_사용자 {
        @BeforeEach
        fun setUp() {
            given(jwtTokenProvider.validateToken(anyString())).willReturn(true)
            given(jwtTokenProvider.parseAuthorizationToken(anyString())).willReturn(AccountJwtPayload(accountId = 1L))
        }

        @Test
        fun `유효한 토큰이면 인증 필요 엔드포인트에 접근할 수 있다`() {
            perform("GET", "/poll/v1/my", authorization = "Bearer valid").andExpect(status().isOk)
            perform("POST", "/poll/v1/public", authorization = "Bearer valid").andExpect(status().isOk)
        }
    }

    @Test
    fun `유효하지 않은 토큰이면 401`() {
        given(jwtTokenProvider.validateToken(anyString())).willReturn(false)
        perform("GET", "/poll/v1/my", authorization = "Bearer invalid").andExpect(status().isUnauthorized)
    }
}

@WebMvcTest(controllers = [SecurityProbeController::class])
@Import(SecurityConfig::class, JwtAuthenticationFilter::class)
@ActiveProfiles("dev")
class SecurityConfigDevProfileTest : SecurityConfigTestBase() {

    @Test
    fun `dev 프로파일에서는 Swagger가 허용된다`() {
        perform("GET", "/v3/api-docs").andExpect(status().isOk)
        perform("GET", "/swagger-ui/index.html").andExpect(status().isOk)
    }
}
