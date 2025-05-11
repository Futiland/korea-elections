package com.futiland.vote.application.account.dto.request

import com.futiland.vote.domain.account.entity.Gender
import java.time.LocalDate

data class SignUpRequest (
    val name: String,
    val phoneNumber: String,
    val password: String,
    val gender: Gender,
    val birthDate: LocalDate,
    val ci: String,
)