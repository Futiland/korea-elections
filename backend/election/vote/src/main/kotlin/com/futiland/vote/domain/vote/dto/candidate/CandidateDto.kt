package com.futiland.vote.domain.vote.dto.candidate

data class CandidateDto(
    val number: Int,
    val name: String,
    val party: String,
    val description: String,
)