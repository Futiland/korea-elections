package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.VoteCommitResponse

interface VoteCommandUseCase {
    fun commit(electionId: Long, candidateId: Long, accountId:Long): VoteCommitResponse
}