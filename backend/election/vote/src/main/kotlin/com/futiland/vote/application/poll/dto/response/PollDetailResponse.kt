package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.*
import java.time.LocalDateTime

data class PollDetailResponse(
    val id: Long,
    val title: String,
    val description: String,
    val responseType: ResponseType,
    val status: PollStatus,
    val isRevotable: Boolean,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val options: List<PollOptionResponse>,
    val creatorInfo: CreatorInfoResponse,
) {
    companion object {
        fun from(poll: Poll, options: List<PollOption>, creatorInfo: CreatorInfoResponse): PollDetailResponse {
            return PollDetailResponse(
                id = poll.id,
                title = poll.title,
                description = poll.description,
                responseType = poll.responseType,
                status = poll.status,
                isRevotable = poll.isRevotable,
                startAt = poll.startAt,
                endAt = poll.endAt,
                createdAt = poll.createdAt,
                options = options.map { PollOptionResponse.from(it) },
                creatorInfo = creatorInfo
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
