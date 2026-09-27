package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.PollResultResponse

interface PollResultQueryUseCase {
    fun getPollResult(pollId: Long, accountId: Long?, anonymousSessionId: String? = null): PollResultResponse
}
