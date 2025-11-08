package com.futiland.vote.application.poll.dto.request

data class PollResponseSubmitRequest(
    val optionIds: List<Long>? = null,
    val scoreValue: Int? = null,
)
