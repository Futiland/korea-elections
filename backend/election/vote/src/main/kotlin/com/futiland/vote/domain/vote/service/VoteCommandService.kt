package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.VoteCommitResponse
import com.futiland.vote.domain.vote.entity.Vote
import com.futiland.vote.domain.vote.repository.VoteRepository
import org.springframework.stereotype.Service

@Service
class VoteCommandService(
    private val voteRepository: VoteRepository,
) : VoteCommandUseCase {
    override fun commit(
        electionId: Long,
        candidateId: Long,
        accountId: Long
    ): VoteCommitResponse {

        val vote = voteRepository.findByElectionIdAndAccountId(electionId, accountId)?.apply {
            update(candidateId)
        } ?: Vote.create(selectedCandidateId = candidateId, electionId = electionId, accountId = accountId)


        val savedVote = voteRepository.save(vote)
        return VoteCommitResponse(
            voteId = savedVote.id
        )
    }
}