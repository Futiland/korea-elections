package com.futiland.vote.domain.vote.dto.result

import com.futiland.vote.domain.account.entity.Gender
import java.time.LocalDateTime

data class GenderGroupResultResponse(
    val electionId: Long,
    val results: List<GenderGroupResult>,
    val updatedAt: LocalDateTime? = null
) :VoteResult{
    var totalVoteCount: Long = results.sumOf { it.candidateResults.sumOf { it.voteCount } }
        private set

    data class GenderGroupResult(
        val genderGroup: Gender,
        val candidateResults: List<CandidateResultDto>
    )
}