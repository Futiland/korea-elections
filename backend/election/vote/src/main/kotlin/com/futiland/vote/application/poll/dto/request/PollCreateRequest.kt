package com.futiland.vote.application.poll.dto.request

import com.futiland.vote.domain.poll.entity.ResponseType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "공개 여론조사 생성 요청")
data class PublicPollCreateRequest(
    @Schema(description = "여론조사 제목", example = "좋아하는 과일은?", required = true)
    val title: String,

    @Schema(description = "여론조사 설명", example = "가장 좋아하는 과일을 선택하세요", required = true)
    val description: String,

    @Schema(
        description = """
            응답 유형
            - SINGLE_CHOICE: 단일 선택
            - MULTIPLE_CHOICE: 다중 선택 (제한 없이 모두 선택 가능)
            - SCORE: 점수제 (기본 0~10점)
        """,
        example = "SINGLE_CHOICE",
        required = true,
        allowableValues = ["SINGLE_CHOICE", "MULTIPLE_CHOICE", "SCORE"]
    )
    val responseType: ResponseType,

    @Schema(description = "재투표 가능 여부 (사용자가 여러 번 투표할 수 있는지)", example = "false", required = true)
    val isRevotable: Boolean,

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

    @Schema(
        description = """
            응답 유형
            - SINGLE_CHOICE: 단일 선택
            - MULTIPLE_CHOICE: 다중 선택 (제한 없이 모두 선택 가능)
            - SCORE: 점수제 (기본 0~10점)
        """,
        example = "SINGLE_CHOICE",
        required = true,
        allowableValues = ["SINGLE_CHOICE", "MULTIPLE_CHOICE", "SCORE"]
    )
    val responseType: ResponseType,

    @Schema(description = "재투표 가능 여부", example = "false", required = true)
    val isRevotable: Boolean,

    @Schema(description = "선택지 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE일 때 필수)", required = false)
    val options: List<PollOptionRequest>? = null,
)

@Schema(description = "시스템 여론조사 생성 요청")
data class SystemPollCreateRequest(
    @Schema(description = "여론조사 제목", example = "2025년 대선 후보 지지율 조사", required = true)
    val title: String,

    @Schema(description = "여론조사 설명", example = "2025년 대선 후보에 대한 지지율을 조사합니다", required = true)
    val description: String,

    @Schema(
        description = """
            응답 유형
            - SINGLE_CHOICE: 단일 선택
            - MULTIPLE_CHOICE: 다중 선택 (제한 없이 모두 선택 가능)
            - SCORE: 점수제 (기본 0~10점)
        """,
        example = "SINGLE_CHOICE",
        required = true,
        allowableValues = ["SINGLE_CHOICE", "MULTIPLE_CHOICE", "SCORE"]
    )
    val responseType: ResponseType,

    @Schema(description = "재투표 가능 여부 (사용자가 여러 번 투표할 수 있는지)", example = "false", required = true)
    val isRevotable: Boolean,

    @Schema(description = "여론조사 종료 일시", example = "2025-01-17T23:59:59", required = true)
    val endAt: LocalDateTime,

    @Schema(description = "선택지 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE일 때 필수)", required = false)
    val options: List<PollOptionRequest>? = null,
)

@Schema(description = "여론조사 선택지")
data class PollOptionRequest(
    @Schema(description = "선택지 텍스트", example = "사과", required = true)
    val optionText: String,

    // TODO: 추후 클라이언트에서 선택지 순서를 직접 조율할 수 있도록 확장 가능
    //  - 현재: optionOrder를 전달하지 않으면 서버에서 리스트 순서대로 자동 부여 (1, 2, 3, ...)
    //  - 미래: 클라이언트에서 optionOrder를 명시적으로 전달하여 원하는 순서로 배치 가능
    //  - 예시: [{"optionText": "바나나", "optionOrder": 2}, {"optionText": "사과", "optionOrder": 1}]
    //         -> 실제 순서: 1.사과, 2.바나나
    @Schema(description = "선택지 순서 (1부터 시작, 미전달 시 자동 부여)", example = "1", required = false)
    val optionOrder: Int? = null,
)
