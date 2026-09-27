package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest

interface PollResponseCommandUseCase {
    fun submitResponse(pollId: Long, accountId: Long?, anonymousSessionId: String?, request: PollResponseSubmitRequest): Long
    fun updateResponse(pollId: Long, accountId: Long?, anonymousSessionId: String?, request: PollResponseSubmitRequest): Long
    fun deleteResponse(pollId: Long, accountId: Long?, anonymousSessionId: String?)
}
