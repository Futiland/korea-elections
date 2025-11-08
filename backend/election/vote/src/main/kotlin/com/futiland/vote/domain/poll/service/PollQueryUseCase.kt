package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.util.SliceContent

interface PollQueryUseCase {
    fun getPollDetail(pollId: Long): PollDetailResponse
    fun getPublicPollList(size: Int, nextCursor: String?): SliceContent<PollListResponse>
    fun getMyPolls(accountId: Long): List<PollListResponse>
}
