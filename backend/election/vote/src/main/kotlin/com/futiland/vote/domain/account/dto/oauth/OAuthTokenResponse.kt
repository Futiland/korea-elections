package com.futiland.vote.domain.account.dto.oauth

import java.time.LocalDateTime

data class OAuthTokenResponse(
    val accessToken: String,
    val refreshToken: String?,
    val accessTokenExpiresIn: Int,
    val refreshTokenExpiresIn: Int?,
    val scope: String?
) {
    fun getAccessTokenExpiresAt(): LocalDateTime {
        return LocalDateTime.now().plusSeconds(accessTokenExpiresIn.toLong())
    }

    fun getRefreshTokenExpiresAt(): LocalDateTime? {
        return refreshTokenExpiresIn?.let {
            LocalDateTime.now().plusSeconds(it.toLong())
        }
    }
}
