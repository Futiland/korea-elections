package com.futiland.vote.domain.vote.service

import com.futiland.vote.domain.vote.entity.Candidate
import com.futiland.vote.domain.vote.repository.CandidateRepository
import org.springframework.stereotype.Service

@Service
class CandidateQueryService(
    private val candidateRepository: CandidateRepository,
) : CandidateQueryUseCase {
    override fun findAllCandidateByElectionId(id: Long): List<Candidate> {
        return candidateRepository.findByElectionId(electionId = id)
    }
}