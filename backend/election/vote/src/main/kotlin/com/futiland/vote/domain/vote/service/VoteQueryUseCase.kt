package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.MyVoteResponse
import com.futiland.vote.domain.vote.dto.result.VoteResultResponse

interface VoteQueryUseCase {
    fun getResult(electionId: Long): VoteResultResponse
    fun findMyVote(electionId: Long, accountId: Long): MyVoteResponse?
}