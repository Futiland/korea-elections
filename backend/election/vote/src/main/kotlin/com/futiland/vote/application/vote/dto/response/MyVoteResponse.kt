package com.futiland.vote.application.vote.dto.response

import java.time.LocalDateTime

data class MyVoteResponse(
    val voteId: Long,
    val electionId: Long,
    val selectedCandidateId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
