package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.OAuthLoginResponse
import com.futiland.vote.domain.account.entity.OAuthProvider

interface OAuthCommandUseCase {
    fun getOAuthLoginUrl(provider: OAuthProvider, redirectUri: String, frontendRedirectUrl: String): String
    fun handleOAuthCallback(
        provider: OAuthProvider,
        code: String,
        state: String,
        redirectUri: String
    ): OAuthLoginResponse
}
