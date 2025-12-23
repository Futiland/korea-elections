package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.MyVoteResponse
import com.futiland.vote.domain.vote.dto.result.VoteResultResponse
import com.futiland.vote.domain.vote.dto.result.CandidateResultDto
import com.futiland.vote.domain.vote.repository.CandidateRepository
import com.futiland.vote.domain.vote.repository.VoteRepository
import org.springframework.stereotype.Service

@Service
class VoteQueryService(
    private val candidateRepository: CandidateRepository,
    private val voteRepository: VoteRepository
) : VoteQueryUseCase {

    override fun getResult(electionId: Long): VoteResultResponse {
        val candidates = candidateRepository.findByElectionId(electionId)
        val candidateResults = candidates.map { candidate ->
            val voteCount = voteRepository.findByElectionIdAndCandidateId(electionId, candidate.id)
            CandidateResultDto(
                id = candidate.id,
                name = candidate.name,
                party = candidate.party.name,
                partyColor = candidate.party.color,
                partyStatus = candidate.party.status,
                number = candidate.number,
                description = candidate.description,
                voteCount = voteCount
            )
        }
        val latestVoteTime = voteRepository.findLatestTimeByElectionId(electionId)
        return VoteResultResponse(
            electionId = electionId,
            results = candidateResults,
            updatedAt = latestVoteTime,
        )
    }

    override fun findMyVote(electionId: Long, accountId: Long): MyVoteResponse? {
        val vote =
            voteRepository.findByElectionIdAndAccountId(electionId = electionId, accountId = accountId) ?: return null
        return MyVoteResponse(
            voteId = vote.id,
            electionId = electionId,
            selectedCandidateId = vote.selectedCandidateId,
            createdAt = vote.createdAt,
            updatedAt = vote.updatedAt
        )
    }
}