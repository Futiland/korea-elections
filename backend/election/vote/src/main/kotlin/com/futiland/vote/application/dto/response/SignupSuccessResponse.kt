package com.futiland.vote.application.dto.response

import java.time.LocalDateTime

data class SignupSuccessResponse(
    val id: Long,
    val createdAt: LocalDateTime
)
