package com.futiland.vote.domain.vote.dto.result

import java.time.LocalDateTime

data class AgeGroupResultResponse(
    val electionId: Long,
    val results: List<AgeGroupResult>,
    val updatedAt: LocalDateTime? = null
):VoteResult{
    var totalVoteCount: Long = results.sumOf { it.candidateResults.sumOf { it.voteCount } }
        private set
    data class AgeGroupResult(
        val ageGroup: AgeGroup,
        val age: Int = ageGroup.age,
        val candidateResults : List<CandidateResultDto>,
        val totalCount: Long,
    )
}
