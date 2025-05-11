package com.futiland.vote.application.vote.dto.response

import java.time.LocalDateTime

data class ElectionCreateResponse(
    val id: Long,
    val createdAt: LocalDateTime,
)
