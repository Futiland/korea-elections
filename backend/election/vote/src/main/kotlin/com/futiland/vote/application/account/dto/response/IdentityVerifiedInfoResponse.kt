package com.futiland.vote.application.account.dto.response

import com.futiland.vote.domain.account.entity.Gender
import java.time.LocalDate

/**
 * 본인인증 최종단계 이후 응답받은 값을 서비스에 맞게 파싱한 값
 */
data class IdentityVerifiedInfoResponse(
    val gender: Gender,
    val birthDate: LocalDate,
    val ci: String, // 본인인증 후 받은 ci
)
