package com.futiland.vote.application.account.port.out

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "kakao-auth", url = "https://kauth.kakao.com")
interface KakaoAuthFeignClient {

    @PostMapping("/oauth/token", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun getAccessToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret", required = false) clientSecret: String?,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestParam("code") code: String
    ): KakaoTokenResponse

    @PostMapping("/oauth/token", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun refreshAccessToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret", required = false) clientSecret: String?,
        @RequestParam("refresh_token") refreshToken: String
    ): KakaoTokenResponse

    data class KakaoTokenResponse(
        val access_token: String,
        val token_type: String,
        val refresh_token: String?,
        val expires_in: Int,
        val refresh_token_expires_in: Int?,
        val scope: String?
    )
}

@FeignClient(name = "kakao-api", url = "https://kapi.kakao.com")
interface KakaoApiFeignClient {

    @GetMapping("/v2/user/me", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun getUserInfo(
        @RequestHeader("Authorization") authorization: String
    ): KakaoUserInfoResponse

    data class KakaoUserInfoResponse(
        val id: Long,
        val kakao_account: KakaoAccount,
        val for_partner: ForPartner?
    ) {
        data class KakaoAccount(
            val profile: Profile?,
            val name: String?,
            val email: String?,
            val phone_number: String?,
            val birthyear: String?,
            val birthday: String?,
            val gender: String?,
            val ci: String?
        ) {
            data class Profile(
                val nickname: String?
            )
        }

        data class ForPartner(
            val uuid: String?
        )
    }
}
