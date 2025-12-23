package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.ParticipatedPollResponse
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent

interface PollQueryUseCase {
    fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse
    fun getPublicPollList(accountId: Long?, size: Int, nextCursor: String?): SliceContent<PollListResponse>

    /**
     * 내가 만든 여론조사 목록 조회 (페이지 기반 방식)
     */
    fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse>

    /**
     * 내가 참여한 투표 목록 조회 (No Offset 페이지네이션 + 전체 개수)
     *
     * @param accountId 계정 ID
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @param pollType 투표 유형 (SYSTEM 또는 PUBLIC)
     */
    fun getParticipatedPolls(accountId: Long, page: Int, size: Int, pollType: PollType): PageContent<ParticipatedPollResponse>

    /**
     * 공개 여론조사 키워드 검색
     */
    fun searchPublicPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<PollListResponse>

    /**
     * 시스템 여론조사 키워드 검색
     */
    fun searchSystemPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<PollListResponse>
}
