package com.futiland.vote.application.account.controller

import com.futiland.vote.application.account.dto.response.OAuthLoginUrlResponse
import com.futiland.vote.application.common.OAuthUrlValidator
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.FrontendProperties
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.entity.OAuthProvider
import com.futiland.vote.domain.account.service.OAuthCommandUseCase
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/account/v1/oauth")
class OAuthController(
    private val oAuthCommandUseCase: OAuthCommandUseCase,
    private val frontendProperties: FrontendProperties,
    @Value("\${oauth.kakao.callback-uri}")
    private val kakaoCallbackUri: String
) {
    /**
     * OAuth 로그인 시작 (카카오 로그인 URL 발급)
     *
     * @param provider OAuth 제공자 (kakao, naver, google)
     * @param frontendRedirectUrl 프론트엔드 리다이렉트 URL (선택)
     *                            - 미제공 시: 첫 번째 등록된 URL 사용
     *                            - 제공 시: 호스트 화이트리스트 검증 후 사용
     *                            - 경로는 자유롭게 지정 가능 (예: /oauth/callback, /auth/success)
     */
    @GetMapping("/{provider}/login")
    fun startOAuthLogin(
        @PathVariable provider: String,
        @RequestParam(required = false) frontendRedirectUrl: String?
    ): HttpApiResponse<OAuthLoginUrlResponse> {
        val oAuthProvider = OAuthProvider.fromProviderName(provider)

        // frontendRedirectUrl 검증 및 기본값 설정
        val validatedFrontendUrl = if (frontendRedirectUrl.isNullOrBlank()) {
            // 미제공 시 첫 번째 등록된 URL 사용
            frontendProperties.urls.firstOrNull()
                ?: throw ApplicationException(
                    code = CodeEnum.FRS_004,
                    message = "설정된 프론트엔드 URL이 없습니다."
                )
        } else {
            // 제공된 URL의 호스트가 화이트리스트에 있는지 검증
            OAuthUrlValidator.validateHostWhitelist(frontendRedirectUrl, frontendProperties.urls)
            frontendRedirectUrl
        }

        // 환경변수에서 설정된 콜백 URI 사용
        val authorizationUrl = oAuthCommandUseCase.getOAuthLoginUrl(
            provider = oAuthProvider,
            redirectUri = kakaoCallbackUri,
            frontendRedirectUrl = validatedFrontendUrl
        )
        return HttpApiResponse.of(OAuthLoginUrlResponse(authorizationUrl))
    }

    @GetMapping("/{provider}/callback")
    fun handleOAuthCallback(
        @PathVariable provider: String,
        @RequestParam code: String,
        @RequestParam state: String
    ): ResponseEntity<Void> {
        val oAuthProvider = OAuthProvider.fromProviderName(provider)

        val response = oAuthCommandUseCase.handleOAuthCallback(
            provider = oAuthProvider,
            code = code,
            state = state,
            redirectUri = kakaoCallbackUri
        )

        // 프론트엔드로 302 리다이렉트 (JWT 토큰과 isNewUser를 쿼리 파라미터로 전달)
        // State에 저장된 frontendRedirectUrl 사용 (기본 경로 포함)
        val redirectUrl = "${response.frontendRedirectUrl}?token=${response.token}&isNewUser=${response.isNewUser}"
        return ResponseEntity.status(302).location(URI.create(redirectUrl)).build()
    }
}
