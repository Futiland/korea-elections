package com.futiland.vote.application.account.controller

import com.futiland.vote.application.account.dto.response.OAuthLoginUrlResponse
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
    @GetMapping("/{provider}/login")
    fun startOAuthLogin(
        @PathVariable provider: String,
        @RequestParam redirectUri: String
    ): HttpApiResponse<OAuthLoginUrlResponse> {
        val oAuthProvider = OAuthProvider.fromProviderName(provider)
        val authorizationUrl = oAuthCommandUseCase.getOAuthLoginUrl(oAuthProvider, redirectUri)
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
        // 첫 번째 등록된 URL 사용
        val frontendUrl = frontendProperties.urls.firstOrNull()
            ?: throw ApplicationException(
                code = CodeEnum.FRS_004,
                message = "설정된 프론트엔드 URL이 없습니다."
            )
        val redirectUrl = "$frontendUrl/oauth/callback.html?token=${response.token}&isNewUser=${response.isNewUser}"
        return ResponseEntity.status(302).location(URI.create(redirectUrl)).build()
    }
}
