package com.futiland.vote.application.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent

interface PollQueryFacadeUseCase {
    fun getPollDetail(pollId: Long, accountId: Long?, anonymousSessionId: String? = null): PollDetailResponse

    /**
     * 여론조사 목록 조회/검색 (타입별)
     *
     * @param pollType 여론조사 타입 (PUBLIC, SYSTEM)
     * @param keyword 검색 키워드 (빈 문자열이면 전체 목록 조회)
     */
    fun getPollListByType(
        pollType: PollType,
        accountId: Long?,
        size: Int,
        nextCursor: String?,
        keyword: String = "",
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL,
        anonymousSessionId: String? = null
    ): SliceContent<PollListResponse>

    fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse>
}
