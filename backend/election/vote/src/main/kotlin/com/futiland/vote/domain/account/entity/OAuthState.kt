package com.futiland.vote.domain.account.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "oauth_state")
class OAuthState(
    @Id
    val state: String = UUID.randomUUID().toString(),
    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(10)
) {
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    fun isValid(): Boolean = !isExpired()
}
