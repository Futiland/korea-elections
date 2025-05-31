package com.futiland.vote.domain.vote.dto.result

import java.time.LocalDateTime

data class VoteResultResponse(
    val electionId: Long,
    val results: List<CandidateResultDto>,
    val totalVoteCount: Long = results.sumOf { it.voteCount },
    val updatedAt: LocalDateTime? = null
) :VoteResult
