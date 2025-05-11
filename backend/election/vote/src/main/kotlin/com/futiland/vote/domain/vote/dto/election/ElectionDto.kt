package com.futiland.vote.domain.vote.dto.election

import com.futiland.vote.domain.vote.entity.ElectionStatus
import java.time.LocalDateTime

data class ElectionDto(
    val title: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val description: String,
    val status: ElectionStatus,
    val createdAt: LocalDateTime,
)
