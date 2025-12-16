package com.futiland.vote.application.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent

interface PollQueryFacadeUseCase {
    fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse

    /**
     * 여론조사 목록 조회 (타입별)
     *
     * @param pollType 여론조사 타입 (PUBLIC, SYSTEM)
     */
    fun getPollListByType(
        pollType: PollType,
        accountId: Long?,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<PollListResponse>

    fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse>

    /**
     * 여론조사 키워드 검색 (타입별)
     *
     * @param pollType 여론조사 타입 (PUBLIC, SYSTEM, null이면 전체)
     */
    fun searchPollsByType(
        pollType: PollType?,
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<PollListResponse>
}
