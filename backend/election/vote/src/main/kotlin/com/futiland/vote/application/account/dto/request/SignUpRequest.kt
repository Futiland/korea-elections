package com.futiland.vote.application.account.dto.request

import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.entity.VerificationType
import java.time.LocalDate

data class SignUpRequest (
    val phoneNumber: String,
    val password: String,
    val verificationId: String,
    val verificationType: VerificationType
)