package com.futiland.vote.application.account.repository

import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.domain.account.entity.OAuthProvider
import com.futiland.vote.domain.account.entity.SocialAccount
import com.futiland.vote.domain.account.repository.SocialAccountRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAccountRepositoryImpl(
    private val jpaSocialAccountRepository: JpaSocialAccountRepository
) : SocialAccountRepository {
    override fun save(socialAccount: SocialAccount) =
        jpaSocialAccountRepository.save(socialAccount)

    override fun findByProviderAndProviderAccountId(
        provider: OAuthProvider,
        providerAccountId: String
    ) = jpaSocialAccountRepository.findByProviderAndProviderAccountId(provider, providerAccountId)

    override fun findActiveByProviderAndProviderAccountId(
        provider: OAuthProvider,
        providerAccountId: String
    ) = jpaSocialAccountRepository.findByProviderAndProviderAccountIdAndStatus(
        provider,
        providerAccountId,
        com.futiland.vote.domain.account.entity.SocialAccountStatus.ACTIVE
    )

    override fun findAllByAccountId(accountId: Long) =
        jpaSocialAccountRepository.findAllByAccountId(accountId)

    override fun getByAccountIdAndProvider(accountId: Long, provider: OAuthProvider) =
        jpaSocialAccountRepository.findByAccountIdAndProvider(accountId, provider)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "연동된 ${provider.providerName} 계정을 찾을 수 없습니다."
            )
}

interface JpaSocialAccountRepository : JpaRepository<SocialAccount, Long> {
    fun findByProviderAndProviderAccountId(
        provider: OAuthProvider,
        providerAccountId: String
    ): SocialAccount?

    fun findByProviderAndProviderAccountIdAndStatus(
        provider: OAuthProvider,
        providerAccountId: String,
        status: com.futiland.vote.domain.account.entity.SocialAccountStatus
    ): SocialAccount?

    fun findAllByAccountId(accountId: Long): List<SocialAccount>

    fun findByAccountIdAndProvider(accountId: Long, provider: OAuthProvider): SocialAccount?
}
