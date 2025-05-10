package com.futiland.vote.domain.account.dto

data class AccountJwtPayload(
    val accountId: Long,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "accountId" to accountId,
        )
    }
}
