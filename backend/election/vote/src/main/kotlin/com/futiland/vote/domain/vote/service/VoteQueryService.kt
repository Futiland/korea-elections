package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.vote.dto.response.MyVoteResponse
import com.futiland.vote.application.vote.dto.response.VoteResultResponse
import com.futiland.vote.domain.vote.dto.candidate.CandidateResultDto
import com.futiland.vote.domain.vote.repository.CandidateRepository
import com.futiland.vote.domain.vote.repository.VoteRepository
import org.springframework.stereotype.Service

@Service
class VoteQueryService(
    private val candidateRepository: CandidateRepository, private val voteRepository: VoteRepository
) : VoteQueryUseCase {

    override fun getResult(electionId: Long): VoteResultResponse {
        val candidates = candidateRepository.findByElectionId(electionId)
        val candidateResults = candidates.map { candidate ->
            val voteCount = voteRepository.findByElectionIdAndCandidateId(electionId, candidate.id)
            CandidateResultDto(
                id = candidate.id,
                name = candidate.name,
                party = candidate.party,
                number = candidate.number,
                description = candidate.description,
                voteCount = voteCount
            )
        }
        return VoteResultResponse(
            electionId = electionId, results = candidateResults
        )
    }

    override fun getMyVote(electionId: Long, accountId: Long): MyVoteResponse {
        val vote = voteRepository.findByElectionIdAndAccountId(electionId = electionId, accountId = accountId)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "해당 선거에 대한 투표 정보가 없습니다. 투표를 해주세요.",
            )
        return MyVoteResponse(
            voteId = vote.id,
            electionId = electionId,
            selectedCandidateId = vote.selectedCandidateId,
            createdAt = vote.createdAt,
            updatedAt = vote.updatedAt
        )
    }
}