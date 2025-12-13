package com.futiland.vote.application.poll.service

import com.futiland.vote.application.poll.dto.response.CreatorInfoResponse
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.application.poll.dto.response.PollOptionResponse
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import org.springframework.stereotype.Service

@Service
class PollQueryFacadeService(
    private val pollRepository: PollRepository,
    private val pollOptionRepository: PollOptionRepository,
    private val pollResponseRepository: PollResponseRepository,
    private val accountRepository: AccountRepository,
) : PollQueryFacadeUseCase {

    override fun getPollDetail(pollId: Long, accountId: Long?): PollDetailResponse {
        val poll = pollRepository.getById(pollId)
        val options = pollOptionRepository.findAllByPollId(pollId)
        val account = accountRepository.getById(poll.creatorAccountId)
        val creatorInfo = CreatorInfoResponse(account.id, account.name)

        val responseCount = pollResponseRepository.countByPollId(pollId)
        val isVoted = accountId?.let {
            pollResponseRepository.findByPollIdAndAccountId(pollId, it) != null
        } ?: false

        return PollDetailResponse.from(poll, options, responseCount, isVoted, creatorInfo)
    }

    override fun getPublicPollList(accountId: Long?, size: Int, nextCursor: String?): SliceContent<PollListResponse> {
        val pollsSlice = pollRepository.findAllPublicDisplayable(size, nextCursor)
        val polls = pollsSlice.content

        val pollIds = polls.map { it.id }

        // N+1 방지: 한 번에 모든 poll의 option 조회
        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)
        val optionsByPollId = allOptions.groupBy { it.pollId }

        // 투표 여부 조회 (비로그인이면 빈 Set)
        val votedPollIds = accountId?.let {
            pollResponseRepository.findVotedPollIds(it, pollIds)
        } ?: emptySet()

        // N+1 방지: 한 번에 모든 작성자 정보 조회
        val creatorIds = polls.map { it.creatorAccountId }.distinct()
        val accountMap = accountRepository.getByIds(creatorIds)

        val pollListResponses = polls.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            val account = accountMap[poll.creatorAccountId]!!
            val creatorInfo = CreatorInfoResponse(account.id, account.name)
            PollListResponse.from(poll, responseCount, options, isVoted = votedPollIds.contains(poll.id), creatorInfo = creatorInfo)
        }
        return SliceContent(pollListResponses, pollsSlice.nextCursor)
    }

    override fun getMyPolls(accountId: Long, page: Int, size: Int): PageContent<PollListResponse> {
        val pollsPage = pollRepository.findMyPollsWithPage(accountId, page, size)
        val polls = pollsPage.content

        val pollIds = polls.map { it.id }

        // N+1 방지: 한 번에 모든 poll의 option 조회
        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)
        val optionsByPollId = allOptions.groupBy { it.pollId }

        // 투표 여부 조회
        val votedPollIds = pollResponseRepository.findVotedPollIds(accountId, pollIds)

        // N+1 방지: 한 번에 모든 작성자 정보 조회
        val creatorIds = polls.map { it.creatorAccountId }.distinct()
        val accountMap = accountRepository.getByIds(creatorIds)

        val pollListResponses = polls.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            val account = accountMap[poll.creatorAccountId]!!
            val creatorInfo = CreatorInfoResponse(account.id, account.name)
            PollListResponse.from(poll, responseCount, options, isVoted = votedPollIds.contains(poll.id), creatorInfo = creatorInfo)
        }

        return PageContent.of(pollListResponses, pollsPage.totalCount, size)
    }
}
