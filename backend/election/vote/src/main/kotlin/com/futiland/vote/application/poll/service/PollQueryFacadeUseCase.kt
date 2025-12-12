package com.futiland.vote.application.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.util.SliceContent

interface PollQueryFacadeUseCase {
    fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse
    fun getPublicPollList(accountId: Long?, size: Int, nextCursor: String?): SliceContent<PollListResponse>
    fun getMyPolls(accountId: Long, size: Int, nextCursor: String?): SliceContent<PollListResponse>
}
