package com.futiland.vote.domain.account.entity

import com.futiland.vote.application.common.EncryptConverter
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "social_account",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["provider", "providerAccountId"])
    ],
    indexes = [
        Index(name = "idx_social_account_status", columnList = "status"),
        Index(name = "idx_social_account_account_id_status", columnList = "accountId,status")
    ]
)
class SocialAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val accountId: Long,
    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,
    val providerAccountId: String,
    accessToken: String,
    refreshToken: String?,
    accessTokenExpiresAt: LocalDateTime,
    refreshTokenExpiresAt: LocalDateTime?,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Convert(converter = EncryptConverter::class)
    var accessToken: String = accessToken
        private set

    @Convert(converter = EncryptConverter::class)
    var refreshToken: String? = refreshToken
        private set

    var accessTokenExpiresAt: LocalDateTime = accessTokenExpiresAt
        private set

    var refreshTokenExpiresAt: LocalDateTime? = refreshTokenExpiresAt
        private set

    @Enumerated(EnumType.STRING)
    var status: SocialAccountStatus = SocialAccountStatus.ACTIVE
        private set

    var updatedAt: LocalDateTime? = null
        private set

    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun create(
            accountId: Long,
            provider: OAuthProvider,
            providerAccountId: String,
            accessToken: String,
            refreshToken: String?,
            accessTokenExpiresAt: LocalDateTime,
            refreshTokenExpiresAt: LocalDateTime?
        ): SocialAccount {
            return SocialAccount(
                accountId = accountId,
                provider = provider,
                providerAccountId = providerAccountId,
                accessToken = accessToken,
                refreshToken = refreshToken,
                accessTokenExpiresAt = accessTokenExpiresAt,
                refreshTokenExpiresAt = refreshTokenExpiresAt
            )
        }
    }

    fun updateTokens(
        accessToken: String,
        refreshToken: String?,
        accessTokenExpiresAt: LocalDateTime,
        refreshTokenExpiresAt: LocalDateTime?
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.accessTokenExpiresAt = accessTokenExpiresAt
        this.refreshTokenExpiresAt = refreshTokenExpiresAt
        this.updatedAt = LocalDateTime.now()
    }

    fun isAccessTokenExpired(): Boolean {
        return LocalDateTime.now().isAfter(accessTokenExpiresAt)
    }

    fun delete() {
        this.status = SocialAccountStatus.INACTIVE
        this.deletedAt = LocalDateTime.now()
    }

    fun reactivate(
        accessToken: String,
        refreshToken: String?,
        accessTokenExpiresAt: LocalDateTime,
        refreshTokenExpiresAt: LocalDateTime?
    ) {
        this.status = SocialAccountStatus.ACTIVE
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.accessTokenExpiresAt = accessTokenExpiresAt
        this.refreshTokenExpiresAt = refreshTokenExpiresAt
        this.deletedAt = null
        this.updatedAt = LocalDateTime.now()
    }
}
