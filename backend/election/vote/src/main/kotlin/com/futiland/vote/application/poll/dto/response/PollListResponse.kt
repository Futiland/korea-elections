package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.ResponseType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "여론조사 목록 응답")
data class PollListResponse(
    @Schema(description = "여론조사 ID", example = "1")
    val id: Long,
    @Schema(description = "여론조사 제목", example = "2024년 대통령 지지율 조사")
    val title: String,
    @Schema(description = "여론조사 설명", example = "현 대통령의 지지율을 조사합니다.")
    val description: String,
    @Schema(description = "응답 유형", example = "SINGLE_CHOICE")
    val responseType: ResponseType,
    @Schema(description = "여론조사 상태", example = "IN_PROGRESS")
    val status: PollStatus,
    @Schema(description = "재투표 가능 여부", example = "true")
    val isRevotable: Boolean,
    @Schema(description = "시작 일시", example = "2024-01-01T00:00:00")
    val startAt: LocalDateTime?,
    @Schema(description = "종료 일시", example = "2024-12-31T23:59:59")
    val endAt: LocalDateTime?,
    @Schema(description = "생성 일시", example = "2024-01-01T00:00:00")
    val createdAt: LocalDateTime,
    @Schema(description = "응답 수", example = "1234")
    val responseCount: Long,
    @Schema(description = "선택지 목록")
    val options: List<PollOptionResponse>,
    @Schema(description = "현재 사용자의 투표 여부 (비로그인 시 false)", example = "true")
    val isVoted: Boolean,
    @Schema(description = "작성자 정보")
    val creatorInfo: CreatorInfoResponse,
) {
    companion object {
        fun from(poll: Poll, responseCount: Long, options: List<PollOptionResponse>, isVoted: Boolean, creatorInfo: CreatorInfoResponse): PollListResponse {
            return PollListResponse(
                id = poll.id,
                title = poll.title,
                description = poll.description,
                responseType = poll.responseType,
                status = poll.status,
                isRevotable = poll.isRevotable,
                startAt = poll.startAt,
                endAt = poll.endAt,
                createdAt = poll.createdAt,
                responseCount = responseCount,
                options = options,
                isVoted = isVoted,
                creatorInfo = creatorInfo
            )
        }
    }
}
