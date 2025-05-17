package com.futiland.vote.domain.vote.service

import com.futiland.vote.domain.vote.entity.Candidate

interface CandidateQueryUseCase {
    fun findAllCandidateByElectionId(
        id:Long
    ):List<Candidate>
}