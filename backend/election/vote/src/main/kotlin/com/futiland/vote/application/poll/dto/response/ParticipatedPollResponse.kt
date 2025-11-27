package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.ResponseType
import java.time.LocalDateTime

/**
 * 내가 참여한 투표 응답
 */
data class ParticipatedPollResponse(
    val pollId: Long,
    val pollTitle: String,
    val pollDescription: String,
    val responseType: ResponseType,
    val pollStatus: PollStatus,
    val isRevotable: Boolean,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val participatedAt: LocalDateTime, // 참여한 시각
    val responseId: Long, // PollResponse ID (다음 페이지 조회용)
) {
    companion object {
        fun from(
            poll: Poll,
            participatedAt: LocalDateTime,
            responseId: Long
        ): ParticipatedPollResponse {
            return ParticipatedPollResponse(
                pollId = poll.id,
                pollTitle = poll.title,
                pollDescription = poll.description,
                responseType = poll.responseType,
                pollStatus = poll.status,
                isRevotable = poll.isRevotable,
                startAt = poll.startAt,
                endAt = poll.endAt,
                participatedAt = participatedAt,
                responseId = responseId
            )
        }
    }
}
