package com.futiland.vote.application.vote.dto.response

import java.time.LocalDateTime

data class CandidateCreateResponse(
    val ids: List<Long>,
    val createdAt: LocalDateTime,
)
