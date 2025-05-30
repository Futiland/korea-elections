package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.CandidateQueryResponse

interface CandidateQueryUseCase {
    fun findAllCandidateByElectionId(
        id:Long
    ):List<CandidateQueryResponse>
}