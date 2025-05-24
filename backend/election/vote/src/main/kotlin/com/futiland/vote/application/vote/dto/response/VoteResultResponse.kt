package com.futiland.vote.application.vote.dto.response

import com.futiland.vote.domain.vote.dto.candidate.CandidateResultDto

data class VoteResultResponse(
    val electionId: Long,
    val results: List<CandidateResultDto>,
    val totalVoteCount: Long = results.sumOf { it.voteCount } // Default to 0 if no candidates
)
