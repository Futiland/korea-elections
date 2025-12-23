package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.ParticipatedPollResponse
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.application.poll.dto.response.PollOptionResponse
import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.repository.AccountForPollRepository
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import org.springframework.stereotype.Service

@Service
class PollQueryService(
    private val pollRepository: PollRepository,
    private val pollOptionRepository: PollOptionRepository,
    private val pollResponseRepository: PollResponseRepository,
    private val accountForPollRepository: AccountForPollRepository,
) : PollQueryUseCase {

    override fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse {
        val poll = pollRepository.getById(pollId)
        val options = pollOptionRepository.findAllByPollId(pollId)
        val creatorInfo = accountForPollRepository.getCreatorInfoById(poll.creatorAccountId)

        val responseCount = pollResponseRepository.countDistinctParticipantsByPollId(pollId)
        val isVoted = accountId?.let {
            pollResponseRepository.findByPollIdAndAccountId(pollId, it) != null
        } ?: false

        return PollDetailResponse.from(poll, options, responseCount, isVoted, creatorInfo)
    }

    override fun getPublicPollList(accountId: Long?, size: Int, nextCursor: String?): SliceContent<PollListResponse> {
        val pollsSlice = pollRepository.findAllPublicDisplayable(size, nextCursor)
        return toPollListResponses(pollsSlice, accountId)
    }

    override fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse> {
        val pollsPage = pollRepository.findMyPollsWithPage(accountId, page, size)
        val polls = pollsPage.content

        val pollIds = polls.map { it.id }

        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)
        val optionsByPollId = allOptions.groupBy { it.pollId }

        val votedPollIds = pollResponseRepository.findVotedPollIds(accountId, pollIds)

        val creatorIds = polls.map { it.creatorAccountId }.distinct()
        val creatorInfoMap = accountForPollRepository.getCreatorInfoByIds(creatorIds)

        val pollListResponses = polls.map { poll ->
            val responseCount = pollResponseRepository.countDistinctParticipantsByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            val creatorInfo = creatorInfoMap[poll.creatorAccountId]!!
            PollListResponse.from(poll, responseCount, options, isVoted = votedPollIds.contains(poll.id), creatorInfo = creatorInfo)
        }

        return PageContent.of(pollListResponses, pollsPage.totalCount, size)
    }

    override fun getParticipatedPolls(accountId: Long, page: Int, size: Int, pollType: PollType): PageContent<ParticipatedPollResponse> {
        val fetchSize = size * 3
        val offset = (page - 1) * size

        val pollResponses = pollResponseRepository.findParticipatedPollsByAccountId(accountId, null, offset + fetchSize)

        val pollIds = pollResponses.map { it.pollId }.distinct()
        val polls = pollRepository.findAllByIdIn(pollIds)
        val pollMap = polls.associateBy { it.id }

        val responseCountMap = pollIds.associateWith { pollId ->
            pollResponseRepository.countDistinctParticipantsByPollId(pollId)
        }

        val allParticipatedPolls = pollResponses.mapNotNull { pollResponse ->
            val poll = pollMap[pollResponse.pollId] ?: return@mapNotNull null
            if (poll.pollType != pollType) return@mapNotNull null

            ParticipatedPollResponse.from(
                poll = poll,
                participatedAt = pollResponse.createdAt,
                responseCount = responseCountMap[poll.id] ?: 0,
                responseId = pollResponse.id
            )
        }

        val totalCount = allParticipatedPolls.size.toLong()

        val pagedContent = allParticipatedPolls
            .drop(offset)
            .take(size)

        return PageContent.of(
            content = pagedContent,
            totalElements = totalCount,
            size = size
        )
    }

    override fun searchPublicPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        val pollsSlice = pollRepository.searchPublicPolls(keyword, size, nextCursor, sortType, statusFilter)
        return toPollListResponses(pollsSlice, accountId)
    }

    override fun searchSystemPolls(
        accountId: Long?,
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<PollListResponse> {
        val pollsSlice = pollRepository.searchSystemPolls(keyword, size, nextCursor, sortType, statusFilter)
        return toPollListResponses(pollsSlice, accountId)
    }

    /**
     * Poll 목록을 PollListResponse 목록으로 변환 (N+1 최적화 포함)
     */
    private fun toPollListResponses(pollsSlice: SliceContent<Poll>, accountId: Long?): SliceContent<PollListResponse> {
        val polls = pollsSlice.content

        val pollIds = polls.map { it.id }

        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)
        val optionsByPollId = allOptions.groupBy { it.pollId }

        val votedPollIds = accountId?.let {
            pollResponseRepository.findVotedPollIds(it, pollIds)
        } ?: emptySet()

        val creatorIds = polls.map { it.creatorAccountId }.distinct()
        val creatorInfoMap = accountForPollRepository.getCreatorInfoByIds(creatorIds)

        val pollListResponses = polls.map { poll ->
            val responseCount = pollResponseRepository.countDistinctParticipantsByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            val creatorInfo = creatorInfoMap[poll.creatorAccountId]!!
            PollListResponse.from(poll, responseCount, options, isVoted = votedPollIds.contains(poll.id), creatorInfo = creatorInfo)
        }
        return SliceContent(pollListResponses, pollsSlice.nextCursor)
    }
}
