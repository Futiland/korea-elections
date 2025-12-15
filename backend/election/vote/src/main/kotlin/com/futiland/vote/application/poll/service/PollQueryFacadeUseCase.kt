package com.futiland.vote.application.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent

interface PollQueryFacadeUseCase {
    fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse
    fun getPublicPollList(
        accountId: Long?,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<PollListResponse>
    fun getSystemPollList(
        accountId: Long?,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<PollListResponse>
    fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse>
}
