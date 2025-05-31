package com.futiland.vote.domain.vote.dto.candidate

data class CandidateDto(
    val number: Int,
    val name: String,
    val partyId: Long,
    val description: String,
)