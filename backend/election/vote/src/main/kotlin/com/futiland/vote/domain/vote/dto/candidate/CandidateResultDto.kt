package com.futiland.vote.domain.vote.dto.candidate

data class CandidateResultDto(
    val id: Long,
    val number: Int,
    val name: String,
    val party: String,
    val description: String,
    val voteCount: Long,
)
