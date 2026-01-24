package com.futiland.vote.domain.account.repository

import com.futiland.vote.domain.account.entity.OAuthProvider
import com.futiland.vote.domain.account.entity.SocialAccount

interface SocialAccountRepository {
    fun save(socialAccount: SocialAccount): SocialAccount
    fun findByProviderAndProviderAccountId(
        provider: OAuthProvider,
        providerAccountId: String
    ): SocialAccount?
    fun findActiveByProviderAndProviderAccountId(
        provider: OAuthProvider,
        providerAccountId: String
    ): SocialAccount?
    fun findAllByAccountId(accountId: Long): List<SocialAccount>
    fun getByAccountIdAndProvider(accountId: Long, provider: OAuthProvider): SocialAccount
}
