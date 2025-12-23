package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.ResponseType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "여론조사 결과 응답")
data class PollResultResponse(
    @Schema(description = "여론조사 ID", example = "1")
    val pollId: Long,
    @Schema(description = "응답 유형", example = "SINGLE_CHOICE")
    val responseType: ResponseType,
    @Schema(description = "전체 응답 수", example = "1234")
    val totalResponseCount: Long,
    @Schema(description = "옵션별 결과 (SINGLE_CHOICE/MULTIPLE_CHOICE인 경우)", nullable = true)
    val optionResults: List<OptionResultResponse>?,
    @Schema(description = "점수 결과 (SCORE인 경우)", nullable = true)
    val scoreResult: ScoreResultResponse?,
    @Schema(description = "내 응답 정보 (responseType에 따라 다른 형태)", nullable = true)
    val myResponse: MyPollResponse?,
)

@Schema(description = "옵션별 결과")
data class OptionResultResponse(
    @Schema(description = "옵션 ID", example = "1")
    val optionId: Long,
    @Schema(description = "옵션 텍스트", example = "매우 찬성")
    val optionText: String,
    @Schema(description = "득표 수", example = "500")
    val voteCount: Long,
    @Schema(description = "득표 비율 (%)", example = "45.5")
    val percentage: Double,
)

@Schema(description = "점수형 결과")
data class ScoreResultResponse(
    @Schema(description = "평균 점수", example = "7.5")
    val averageScore: Double,
    @Schema(description = "최소 점수", example = "1")
    val minScore: Int,
    @Schema(description = "최대 점수", example = "10")
    val maxScore: Int,
    @Schema(description = "점수별 분포 (key: 점수, value: 응답 수)", example = "{\"1\": 10, \"5\": 50, \"10\": 30}")
    val scoreDistribution: Map<Int, Long>,
)
