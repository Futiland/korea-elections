package com.futiland.vote.application.poll.dto.request

import com.futiland.vote.domain.poll.entity.QuestionType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "공개 여론조사 생성 요청")
data class PublicPollCreateRequest(
    @Schema(description = "여론조사 제목", example = "좋아하는 과일은?", required = true)
    val title: String,

    @Schema(description = "여론조사 설명", example = "가장 좋아하는 과일을 선택하세요", required = true)
    val description: String,

    @Schema(description = "질문 유형", example = "SINGLE_CHOICE", required = true,
        allowableValues = ["SINGLE_CHOICE", "MULTIPLE_CHOICE", "SCORE"])
    val questionType: QuestionType,

    @Schema(description = "중복 응답 허용 여부 (사용자가 여러 번 응답할 수 있는지)", example = "false", required = true)
    val allowMultipleResponses: Boolean,

    @Schema(description = "최소 선택 개수 (MULTIPLE_CHOICE일 때만 사용)", example = "2", required = false)
    val minSelections: Int? = null,

    @Schema(description = "최대 선택 개수 (MULTIPLE_CHOICE일 때만 사용)", example = "3", required = false)
    val maxSelections: Int? = null,

    @Schema(description = "최소 점수 (SCORE일 때만 사용)", example = "0", required = true, defaultValue = "0")
    val minScore: Int = 0,

    @Schema(description = "최대 점수 (SCORE일 때만 사용)", example = "10", required = true, defaultValue = "10")
    val maxScore: Int = 10,

    @Schema(description = "여론조사 시작 일시", example = "2025-01-10T00:00:00", required = true)
    val startAt: LocalDateTime,

    @Schema(description = "여론조사 종료 일시", example = "2025-01-17T23:59:59", required = true)
    val endAt: LocalDateTime,

    @Schema(description = "선택지 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE일 때 필수)", required = false)
    val options: List<PollOptionRequest>? = null,
)

@Schema(description = "공개 여론조사 임시저장 요청 (시작/종료 시간 없이 DRAFT 상태로 저장)")
data class PublicPollDraftCreateRequest(
    @Schema(description = "여론조사 제목", example = "좋아하는 색은?", required = true)
    val title: String,

    @Schema(description = "여론조사 설명", example = "작성 중", required = true)
    val description: String,

    @Schema(description = "질문 유형", example = "SINGLE_CHOICE", required = true,
        allowableValues = ["SINGLE_CHOICE", "MULTIPLE_CHOICE", "SCORE"])
    val questionType: QuestionType,

    @Schema(description = "중복 응답 허용 여부", example = "false", required = true)
    val allowMultipleResponses: Boolean,

    @Schema(description = "최소 선택 개수 (MULTIPLE_CHOICE일 때만 사용)", example = "2", required = false)
    val minSelections: Int? = null,

    @Schema(description = "최대 선택 개수 (MULTIPLE_CHOICE일 때만 사용)", example = "3", required = false)
    val maxSelections: Int? = null,

    @Schema(description = "최소 점수 (SCORE일 때만 사용)", example = "0", required = true, defaultValue = "0")
    val minScore: Int = 0,

    @Schema(description = "최대 점수 (SCORE일 때만 사용)", example = "10", required = true, defaultValue = "10")
    val maxScore: Int = 10,

    @Schema(description = "선택지 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE일 때 필수)", required = false)
    val options: List<PollOptionRequest>? = null,
)

@Schema(description = "여론조사 선택지")
data class PollOptionRequest(
    @Schema(description = "선택지 텍스트", example = "사과", required = true)
    val optionText: String,

    @Schema(description = "선택지 순서 (1부터 시작)", example = "1", required = true)
    val optionOrder: Int,
)
