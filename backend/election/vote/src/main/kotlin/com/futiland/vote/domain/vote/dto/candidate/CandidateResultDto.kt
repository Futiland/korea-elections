package com.futiland.vote.domain.vote.dto.candidate

import com.futiland.vote.domain.vote.entity.PartyStatus

data class CandidateResultDto(
    val id: Long,
    val number: Int,
    val name: String,
    val party: String,
    val partyColor: String,
    val partyStatus: PartyStatus,
    val description: String,
    val voteCount: Long,
)