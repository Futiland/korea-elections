package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.CandidateQueryResponse
import com.futiland.vote.domain.vote.repository.CandidateRepository
import org.springframework.stereotype.Service

@Service
class CandidateQueryService(
    private val candidateRepository: CandidateRepository,
) : CandidateQueryUseCase {
    override fun findAllCandidateByElectionId(id: Long): List<CandidateQueryResponse> {
        return candidateRepository.findByElectionId(electionId = id).map {
            CandidateQueryResponse(
                electionId = it.electionId,
                id = it.id,
                name = it.name,
                number = it.number,
                party = it.party.name,
                partyColor = it.party.color,
                partyStatus = it.party.status,
                description = it.description,
                createdAt = it.createdAt,
                deletedAt = it.deletedAt
            )
        }
    }
}