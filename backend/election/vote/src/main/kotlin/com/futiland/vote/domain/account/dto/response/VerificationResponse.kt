package com.futiland.vote.domain.account.dto.response

import java.time.LocalDate

data class VerificationResponse(
    val status: String,
    val id: String,
    val channel: Channel,
    val verifiedCustomer: VerifiedCustomer,
    val requestedAt: String,
    val updatedAt: String,
    val statusChangedAt: String,
    val verifiedAt: String,
    val pgTxId: String,
    val pgRawResponse: String,
    val version: String
) {
    data class Channel(
        val type: String,
        val id: String,
        val key: String,
        val name: String,
        val pgProvider: String,
        val pgMerchantId: String
    )

    data class VerifiedCustomer(
        val id: String,
        val name: String,
        val operator: String,
        val phoneNumber: String,
        val birthDate: LocalDate,
        val gender: String,
        val isForeigner: Boolean,
        val ci: String,
        val di: String
    )
}
