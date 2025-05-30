package com.futiland.vote.application.vote.dto.response

import com.futiland.vote.domain.vote.entity.PartyStatus
import java.time.LocalDateTime

data class CandidateQueryResponse(
    val electionId: Long,
    val id: Long,
    val name: String,
    val number: Int,
    val party: String,
    val partyColor: String,
    val partyStatus: PartyStatus,
    val description: String,
    val createdAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
)