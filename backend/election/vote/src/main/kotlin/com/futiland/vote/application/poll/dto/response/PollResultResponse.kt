package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.QuestionType

data class PollResultResponse(
    val pollId: Long,
    val questionType: QuestionType,
    val totalResponseCount: Long,
    val optionResults: List<OptionResultResponse>?,
    val scoreResult: ScoreResultResponse?,
)

data class OptionResultResponse(
    val optionId: Long,
    val optionText: String,
    val voteCount: Long,
    val percentage: Double,
)

data class ScoreResultResponse(
    val averageScore: Double,
    val minScore: Int,
    val maxScore: Int,
    val scoreDistribution: Map<Int, Long>,
)
