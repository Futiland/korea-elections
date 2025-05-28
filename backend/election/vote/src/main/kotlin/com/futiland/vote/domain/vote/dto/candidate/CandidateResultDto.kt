package com.futiland.vote.domain.vote.dto.candidate

import com.futiland.vote.domain.vote.entity.Party

data class CandidateResultDto(
    val id: Long,
    val number: Int,
    val name: String,
    val party: Party,
    val description: String,
    val voteCount: Long,
)
