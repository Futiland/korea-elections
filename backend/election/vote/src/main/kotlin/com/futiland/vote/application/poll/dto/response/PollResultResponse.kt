package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.domain.poll.entity.ResponseType

data class PollResultResponse(
    val pollId: Long,
    val responseType: ResponseType,
    val totalResponseCount: Long,
    val optionResults: List<OptionResultResponse>?,
    val scoreResult: ScoreResultResponse?,
    val myResponse: MyPollResponse?,
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
