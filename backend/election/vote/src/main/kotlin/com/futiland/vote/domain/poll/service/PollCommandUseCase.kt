package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PublicPollDraftCreateRequest
import com.futiland.vote.application.poll.dto.request.PollUpdateRequest
import com.futiland.vote.application.poll.dto.request.SystemPollCreateRequest
import com.futiland.vote.application.poll.dto.response.PollDetailResponse

interface PollCommandUseCase {
    fun createPublicPoll(request: PublicPollCreateRequest, creatorAccountId: Long): PollDetailResponse
    fun createPublicPollDraft(request: PublicPollDraftCreateRequest, creatorAccountId: Long): PollDetailResponse
    fun createSystemPoll(request: SystemPollCreateRequest, creatorAccountId: Long): PollDetailResponse
    fun updatePoll(pollId: Long, request: PollUpdateRequest): PollDetailResponse
    fun deletePoll(pollId: Long, accountId: Long)
    fun cancelPoll(pollId: Long, accountId: Long)
}
