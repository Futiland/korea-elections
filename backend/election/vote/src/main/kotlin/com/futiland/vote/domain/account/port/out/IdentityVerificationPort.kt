package com.futiland.vote.domain.account.port.out

import com.futiland.vote.domain.account.dto.response.VerificationResponse

interface IdentityVerificationPort {
    fun verify(identityVerificationId: String): VerificationResponse
}