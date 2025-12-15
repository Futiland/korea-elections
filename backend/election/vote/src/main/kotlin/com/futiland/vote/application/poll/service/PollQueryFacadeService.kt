package com.futiland.vote.application.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.service.PollQueryUseCase
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import org.springframework.stereotype.Service

/**
 * Poll 조회 Facade Service
 *
 * 여러 도메인의 UseCase를 조합하여 Poll 조회 기능을 제공합니다.
 * Facade 패턴에 따라 UseCase만 주입받아 사용합니다.
 */
@Service
class PollQueryFacadeService(
    private val pollQueryUseCase: PollQueryUseCase,
) : PollQueryFacadeUseCase {

    override fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse {
        return pollQueryUseCase.getPollDetail(pollId, accountId)
    }

    override fun getPublicPollList(
        accountId: Long?,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        return pollQueryUseCase.searchPublicPolls(
            accountId = accountId,
            keyword = "",
            size = size,
            nextCursor = nextCursor,
            sortType = sortType,
            statusFilter = statusFilter
        )
    }

    override fun getSystemPollList(
        accountId: Long?,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        return pollQueryUseCase.searchSystemPolls(
            accountId = accountId,
            keyword = "",
            size = size,
            nextCursor = nextCursor,
            sortType = sortType,
            statusFilter = statusFilter
        )
    }

    override fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse> {
        return pollQueryUseCase.getMyPolls(accountId, page, size)
    }

    override fun searchPublicPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        return pollQueryUseCase.searchPublicPolls(accountId, keyword, size, nextCursor, sortType, statusFilter)
    }

    override fun searchSystemPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        return pollQueryUseCase.searchSystemPolls(accountId, keyword, size, nextCursor, sortType, statusFilter)
    }

    override fun searchAllPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        return pollQueryUseCase.searchAllPolls(accountId, keyword, size, nextCursor, sortType, statusFilter)
    }
}
