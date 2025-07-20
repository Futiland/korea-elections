package com.futiland.vote.application.account.dto.request

import com.futiland.vote.domain.account.entity.VerificationType

data class VerificationRequest(
    val verificationId: String,
    val verificationType: VerificationType
)
