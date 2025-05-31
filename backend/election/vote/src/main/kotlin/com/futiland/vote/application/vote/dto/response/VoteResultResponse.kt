package com.futiland.vote.application.vote.dto.response

import com.futiland.vote.domain.vote.dto.candidate.CandidateResultDto
import java.time.LocalDateTime

data class VoteResultResponse(
    val electionId: Long,
    val results: List<CandidateResultDto>,
    val totalVoteCount: Long = results.sumOf { it.voteCount },
    val updatedAt: LocalDateTime? = null
)
