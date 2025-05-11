package com.futiland.vote.application.vote.dto.request

data class CandidateDeleteRequest(
    val electionId: Long,
    val candidateIds: List<Long>
)
