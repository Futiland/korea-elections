package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.*
import java.time.LocalDateTime

data class PollDetailResponse(
    val id: Long,
    val title: String,
    val description: String,
    val questionType: QuestionType,
    val status: PollStatus,
    val allowMultipleResponses: Boolean,
    val minSelections: Int?,
    val maxSelections: Int?,
    val minScore: Int,
    val maxScore: Int,
    val creatorAccountId: Long,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val options: List<PollOptionResponse>,
) {
    companion object {
        fun from(poll: Poll, options: List<PollOption>): PollDetailResponse {
            return PollDetailResponse(
                id = poll.id,
                title = poll.title,
                description = poll.description,
                questionType = poll.questionType,
                status = poll.status,
                allowMultipleResponses = poll.allowMultipleResponses,
                minSelections = poll.minSelections,
                maxSelections = poll.maxSelections,
                minScore = poll.minScore,
                maxScore = poll.maxScore,
                creatorAccountId = poll.creatorAccountId,
                startAt = poll.startAt,
                endAt = poll.endAt,
                createdAt = poll.createdAt,
                options = options.map { PollOptionResponse.from(it) }
            )
        }
    }
}

data class PollOptionResponse(
    val id: Long,
    val optionText: String,
    val optionOrder: Int,
) {
    companion object {
        fun from(pollOption: PollOption): PollOptionResponse {
            return PollOptionResponse(
                id = pollOption.id,
                optionText = pollOption.optionText,
                optionOrder = pollOption.optionOrder
            )
        }
    }
}
