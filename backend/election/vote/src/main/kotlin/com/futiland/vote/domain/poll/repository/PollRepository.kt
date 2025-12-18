package com.futiland.vote.domain.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import java.time.LocalDateTime

interface PollRepository {
    fun save(poll: Poll): Poll
    fun getById(id: Long): Poll
    fun findById(id: Long): Poll?
    fun findAllPublicDisplayable(
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<Poll>
    fun findAllSystemDisplayable(
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<Poll>

    /**
     * ID 목록으로 여론조사 조회 (삭제된 것 제외)
     */
    fun findAllByIdIn(ids: List<Long>): List<Poll>

    /**
     * 내가 만든 여론조사 목록 조회 (페이지 기반 방식)
     */
    fun findMyPollsWithPage(creatorAccountId: Long, page: Int, size: Int): PageContent<Poll>

    /**
     * 만료 기한이 지난 진행중 여론조사를 만료 상태로 일괄 변경
     * @return 업데이트된 건수
     */
    fun expireOverduePolls(now: LocalDateTime): Int

    /**
     * 공개 여론조사 키워드 검색
     */
    fun searchPublicPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<Poll>

    /**
     * 시스템 여론조사 키워드 검색
     */
    fun searchSystemPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType = PollSortType.LATEST,
        statusFilter: PollStatusFilter = PollStatusFilter.ALL
    ): SliceContent<Poll>
}
