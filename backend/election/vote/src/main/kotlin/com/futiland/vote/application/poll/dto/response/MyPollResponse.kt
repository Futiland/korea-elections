package com.futiland.vote.application.poll.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(
    description = "내 응답 정보 (responseType에 따라 다른 형태)",
    oneOf = [
        MyPollResponse.SingleChoice::class,
        MyPollResponse.MultipleChoice::class,
        MyPollResponse.Score::class
    ]
)
sealed interface MyPollResponse {
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?

    @Schema(description = "단일 선택형 응답")
    data class SingleChoice(
        @Schema(description = "선택한 옵션 ID", example = "1")
        val selectedOptionId: Long,
        @Schema(description = "응답 생성 일시", example = "2024-01-15T10:30:00")
        override val createdAt: LocalDateTime,
        @Schema(description = "응답 수정 일시 (수정한 적 없으면 null)", example = "2024-01-15T11:00:00", nullable = true)
        override val updatedAt: LocalDateTime?
    ) : MyPollResponse

    @Schema(description = "복수 선택형 응답")
    data class MultipleChoice(
        @Schema(description = "선택한 옵션 ID 목록", example = "[1, 3, 5]")
        val selectedOptionIds: List<Long>,
        @Schema(description = "응답 생성 일시", example = "2024-01-15T10:30:00")
        override val createdAt: LocalDateTime,
        @Schema(description = "응답 수정 일시 (수정한 적 없으면 null)", example = "2024-01-15T11:00:00", nullable = true)
        override val updatedAt: LocalDateTime?
    ) : MyPollResponse

    @Schema(description = "점수형 응답")
    data class Score(
        @Schema(description = "입력한 점수", example = "8")
        val scoreValue: Int,
        @Schema(description = "응답 생성 일시", example = "2024-01-15T10:30:00")
        override val createdAt: LocalDateTime,
        @Schema(description = "응답 수정 일시 (수정한 적 없으면 null)", example = "2024-01-15T11:00:00", nullable = true)
        override val updatedAt: LocalDateTime?
    ) : MyPollResponse
}
