package com.futiland.vote.application.account.port.out

import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.domain.account.dto.oauth.OAuthTokenResponse
import com.futiland.vote.domain.account.dto.oauth.OAuthUserInfoResponse
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.entity.OAuthProvider
import com.futiland.vote.domain.account.port.out.OAuthProviderPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class KakaoOAuthProviderPortImpl(
    private val kakaoAuthClient: KakaoAuthFeignClient,
    private val kakaoApiClient: KakaoApiFeignClient,
    @Value("\${oauth.kakao.client-id}")
    private val clientId: String,
    @Value("\${oauth.kakao.client-secret}")
    private val clientSecret: String
) : OAuthProviderPort {

    companion object {
        private const val AUTH_URL = "https://kauth.kakao.com/oauth/authorize"
    }

    override fun getProvider(): OAuthProvider = OAuthProvider.KAKAO

    override fun getAuthorizationUrl(state: String, redirectUri: String): String {
        val scope = listOf(
            "name",
            "phone_number",
            "gender",
            "birthyear",
            "birthday",
            "account_ci"
        ).joinToString(",")

        return "$AUTH_URL?" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "response_type=code&" +
                "state=$state&" +
                "scope=$scope"
    }

    override fun getAccessToken(code: String, redirectUri: String): OAuthTokenResponse {
        return try {
            val response = kakaoAuthClient.getAccessToken(
                grantType = "authorization_code",
                clientId = clientId,
                clientSecret = if (clientSecret.isNotBlank()) clientSecret else null,
                redirectUri = redirectUri,
                code = code
            )
            response.toOAuthTokenResponse()
        } catch (e: Exception) {
            throw ApplicationException(
                code = CodeEnum.FRS_004,
                message = "카카오 토큰 발급 중 오류 발생: ${e.message}"
            )
        }
    }

    override fun getUserInfo(accessToken: String): OAuthUserInfoResponse {
        return try {
            val response = kakaoApiClient.getUserInfo("Bearer $accessToken")
            response.toOAuthUserInfoResponse()
        } catch (e: Exception) {
            throw ApplicationException(
                code = CodeEnum.FRS_004,
                message = "카카오 사용자 정보 조회 중 오류 발생: ${e.message}"
            )
        }
    }

    override fun refreshAccessToken(refreshToken: String): OAuthTokenResponse {
        return try {
            val response = kakaoAuthClient.refreshAccessToken(
                grantType = "refresh_token",
                clientId = clientId,
                clientSecret = if (clientSecret.isNotBlank()) clientSecret else null,
                refreshToken = refreshToken
            )
            response.toOAuthTokenResponse()
        } catch (e: Exception) {
            throw ApplicationException(
                code = CodeEnum.FRS_004,
                message = "카카오 토큰 갱신 중 오류 발생: ${e.message}"
            )
        }
    }

    // Extension functions
    private fun KakaoAuthFeignClient.KakaoTokenResponse.toOAuthTokenResponse() = OAuthTokenResponse(
        accessToken = access_token,
        refreshToken = refresh_token,
        accessTokenExpiresIn = expires_in,
        refreshTokenExpiresIn = refresh_token_expires_in,
        scope = scope
    )

    private fun KakaoApiFeignClient.KakaoUserInfoResponse.toOAuthUserInfoResponse(): OAuthUserInfoResponse {
        val phoneNumber = kakao_account.phone_number
            ?.replace("+82 ", "0")
            ?.replace("-", "")
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "카카오에서 전화번호 정보를 제공하지 않았습니다."
            )

        val name = kakao_account.name
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "카카오에서 이름 정보를 제공하지 않았습니다."
            )

        val ci = kakao_account.ci
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "카카오에서 CI 정보를 제공하지 않았습니다."
            )

        val birthyear = kakao_account.birthyear?.toIntOrNull()
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "카카오에서 생년 정보를 제공하지 않았습니다."
            )

        val birthday = kakao_account.birthday
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "카카오에서 생일 정보를 제공하지 않았습니다."
            )

        // birthday format: MMDD
        val month = birthday.substring(0, 2).toInt()
        val day = birthday.substring(2, 4).toInt()
        val birthDate = LocalDate.of(birthyear, month, day)

        val gender = when (kakao_account.gender) {
            "male" -> Gender.MALE
            "female" -> Gender.FEMALE
            else -> throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "카카오에서 성별 정보를 제공하지 않았습니다."
            )
        }

        return OAuthUserInfoResponse(
            id = id.toString(),
            name = name,
            phoneNumber = phoneNumber,
            gender = gender,
            birthDate = birthDate,
            ci = ci
        )
    }
}

