package com.futiland.vote.application.poll.dto.request

import java.time.LocalDateTime

data class PollUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
)
