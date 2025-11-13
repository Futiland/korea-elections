package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollOption
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.ResponseType
import java.time.LocalDateTime

data class PollListResponse(
    val id: Long,
    val title: String,
    val description: String,
    val responseType: ResponseType,
    val status: PollStatus,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val responseCount: Long,
    val options: List<PollOptionResponse>,
) {
    companion object {
        fun from(poll: Poll, responseCount: Long, options: List<PollOptionResponse>): PollListResponse {
            return PollListResponse(
                id = poll.id,
                title = poll.title,
                description = poll.description,
                responseType = poll.responseType,
                status = poll.status,
                startAt = poll.startAt,
                endAt = poll.endAt,
                createdAt = poll.createdAt,
                responseCount = responseCount,
                options = options
            )
        }
    }
}
