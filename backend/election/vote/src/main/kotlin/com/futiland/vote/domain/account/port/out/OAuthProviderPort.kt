package com.futiland.vote.domain.account.port.out

import com.futiland.vote.domain.account.dto.oauth.OAuthTokenResponse
import com.futiland.vote.domain.account.dto.oauth.OAuthUserInfoResponse
import com.futiland.vote.domain.account.entity.OAuthProvider

interface OAuthProviderPort {
    fun getProvider(): OAuthProvider
    fun getAuthorizationUrl(state: String, redirectUri: String): String
    fun getAccessToken(code: String, redirectUri: String): OAuthTokenResponse
    fun getUserInfo(accessToken: String): OAuthUserInfoResponse
    fun refreshAccessToken(refreshToken: String): OAuthTokenResponse
}
