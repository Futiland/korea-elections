package com.futiland.vote.domain.account.service

import com.futiland.vote.domain.account.dto.response.VerifiedResponse

interface MobileIdentifyUseCase {
    fun verify(identityVerificationId: String): VerifiedResponse
}