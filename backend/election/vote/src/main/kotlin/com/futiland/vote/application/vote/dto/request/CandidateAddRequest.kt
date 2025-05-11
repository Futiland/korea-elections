package com.futiland.vote.application.vote.dto.request

import com.futiland.vote.domain.vote.dto.candidate.CandidateDto

data class CandidateAddRequest(
    val electionId: Long,
    val candidates: List<CandidateDto>
)
