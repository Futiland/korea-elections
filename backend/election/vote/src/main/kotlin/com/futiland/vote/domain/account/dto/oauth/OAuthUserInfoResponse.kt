package com.futiland.vote.domain.account.dto.oauth

import com.futiland.vote.domain.account.entity.Gender
import java.time.LocalDate

data class OAuthUserInfoResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val gender: Gender,
    val birthDate: LocalDate,
    val ci: String
)
