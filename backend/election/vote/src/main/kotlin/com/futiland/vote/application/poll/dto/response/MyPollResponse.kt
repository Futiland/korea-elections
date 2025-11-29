package com.futiland.vote.application.poll.dto.response

import java.time.LocalDateTime

sealed interface MyPollResponse {
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?

    data class SingleChoice(
        val selectedOptionId: Long,
        override val createdAt: LocalDateTime,
        override val updatedAt: LocalDateTime?
    ) : MyPollResponse

    data class MultipleChoice(
        val selectedOptionIds: List<Long>,
        override val createdAt: LocalDateTime,
        override val updatedAt: LocalDateTime?
    ) : MyPollResponse

    data class Score(
        val scoreValue: Int,
        override val createdAt: LocalDateTime,
        override val updatedAt: LocalDateTime?
    ) : MyPollResponse
}
