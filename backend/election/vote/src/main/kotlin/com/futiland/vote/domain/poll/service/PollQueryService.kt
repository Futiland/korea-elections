package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.CreatorInfoResponse
import com.futiland.vote.application.poll.dto.response.ParticipatedPollResponse
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
class PollQueryService(
    private val pollRepository: PollRepository,
    private val pollOptionRepository: PollOptionRepository,
    private val pollResponseRepository: PollResponseRepository,
    private val accountRepository: AccountRepository,
) : PollQueryUseCase {

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

        // 모든 poll의 ID 추출
        val pollIds = polls.map { it.id }

        // 한 번에 모든 poll의 option 조회 (N+1 문제 해결)
        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)

        // pollId별로 option 그룹화
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

    override fun getMyPolls(accountId: Long, size: Int, nextCursor: String?): SliceContent<PollListResponse> {
        val pollsSlice = pollRepository.findMyPolls(accountId, size, nextCursor?.toLongOrNull())
        val polls = pollsSlice.content

        // 모든 poll의 ID 추출
        val pollIds = polls.map { it.id }

        // 한 번에 모든 poll의 option 조회 (N+1 문제 해결)
        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)

        // pollId별로 option 그룹화
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

        return SliceContent(pollListResponses, pollsSlice.nextCursor)
    }

    override fun getParticipatedPolls(accountId: Long, page: Int, size: Int, pollType: com.futiland.vote.domain.poll.entity.PollType): PageContent<ParticipatedPollResponse> {
        // pollType에 맞는 데이터만 필터링하기 위해 충분한 양을 조회
        // TODO: 성능 최적화 - pollType을 포함한 쿼리로 개선 필요
        val fetchSize = size * 3 // 여유있게 조회
        val offset = (page - 1) * size

        val pollResponses = pollResponseRepository.findParticipatedPollsByAccountId(accountId, null, offset + fetchSize)

        // Poll 정보 조회 (N+1 문제 해결)
        val pollIds = pollResponses.map { it.pollId }.distinct()
        val polls = pollRepository.findAllByIdIn(pollIds)
        val pollMap = polls.associateBy { it.id }

        // 각 poll의 응답 수 조회 (N+1 문제 해결)
        val responseCountMap = pollIds.associateWith { pollId ->
            pollResponseRepository.countByPollId(pollId)
        }

        // pollType으로 필터링하고 ParticipatedPollResponse 생성
        val allParticipatedPolls = pollResponses.mapNotNull { pollResponse ->
            val poll = pollMap[pollResponse.pollId] ?: return@mapNotNull null
            if (poll.pollType != pollType) return@mapNotNull null // pollType 필터링

            ParticipatedPollResponse.from(
                poll = poll,
                participatedAt = pollResponse.createdAt,
                responseCount = responseCountMap[poll.id] ?: 0,
                responseId = pollResponse.id
            )
        }

        // 전체 개수 (해당 pollType만)
        val totalCount = allParticipatedPolls.size.toLong()

        // 페이지네이션 적용
        val pagedContent = allParticipatedPolls
            .drop(offset)
            .take(size)

        return PageContent.of(
            content = pagedContent,
            totalElements = totalCount,
            size = size
        )
    }
}
