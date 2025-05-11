package com.futiland.vote.application.vote.dto.response

import java.time.LocalDateTime

data class ElectionDeleteResponse(
    val id: Long,
    val deletedAt: LocalDateTime,
)
