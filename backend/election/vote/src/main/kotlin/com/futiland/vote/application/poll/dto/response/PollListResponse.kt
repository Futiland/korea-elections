package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.QuestionType
import java.time.LocalDateTime

data class PollListResponse(
    val id: Long,
    val title: String,
    val description: String,
    val questionType: QuestionType,
    val status: PollStatus,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val responseCount: Long,
) {
    companion object {
        fun from(poll: Poll, responseCount: Long): PollListResponse {
            return PollListResponse(
                id = poll.id,
                title = poll.title,
                description = poll.description,
                questionType = poll.questionType,
                status = poll.status,
                startAt = poll.startAt,
                endAt = poll.endAt,
                createdAt = poll.createdAt,
                responseCount = responseCount
            )
        }
    }
}
