package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.VoteResultResponse

interface VoteQueryUseCase {
    fun getResult(electionId: Long): VoteResultResponse
}