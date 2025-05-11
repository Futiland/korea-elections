package com.futiland.vote.application.vote.dto.request

import java.time.LocalDateTime

data class ElectionCreateRequest(
    val title: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val description: String,
)
