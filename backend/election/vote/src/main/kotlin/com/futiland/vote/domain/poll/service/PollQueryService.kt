package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.ParticipatedPollResponse
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.application.poll.dto.response.PollOptionResponse
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
) : PollQueryUseCase {

    override fun getPollDetail(pollId: Long): PollDetailResponse {
        val poll = pollRepository.getById(pollId)
        val options = pollOptionRepository.findAllByPollId(pollId)
        return PollDetailResponse.from(poll, options)
    }

    override fun getPublicPollList(size: Int, nextCursor: String?): SliceContent<PollListResponse> {
        val pollsSlice = pollRepository.findAllPublicDisplayable(size, nextCursor)
        val polls = pollsSlice.content

        // 모든 poll의 ID 추출
        val pollIds = polls.map { it.id }

        // 한 번에 모든 poll의 option 조회 (N+1 문제 해결)
        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)

        // pollId별로 option 그룹화
        val optionsByPollId = allOptions.groupBy { it.pollId }

        val pollListResponses = polls.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            PollListResponse.from(poll, responseCount, options)
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

        val pollListResponses = polls.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            PollListResponse.from(poll, responseCount, options)
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

        // pollType으로 필터링하고 ParticipatedPollResponse 생성
        val allParticipatedPolls = pollResponses.mapNotNull { pollResponse ->
            val poll = pollMap[pollResponse.pollId] ?: return@mapNotNull null
            if (poll.pollType != pollType) return@mapNotNull null // pollType 필터링

            ParticipatedPollResponse.from(
                poll = poll,
                participatedAt = pollResponse.createdAt,
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
