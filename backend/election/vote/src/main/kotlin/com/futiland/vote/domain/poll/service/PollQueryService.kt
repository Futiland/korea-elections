package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.application.poll.dto.response.PollOptionResponse
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
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

    override fun getMyPolls(accountId: Long): List<PollListResponse> {
        val polls = pollRepository.findAllByCreatorAccountId(accountId)

        // 모든 poll의 ID 추출
        val pollIds = polls.map { it.id }

        // 한 번에 모든 poll의 option 조회 (N+1 문제 해결)
        val allOptions = pollOptionRepository.findAllByPollIdIn(pollIds)

        // pollId별로 option 그룹화
        val optionsByPollId = allOptions.groupBy { it.pollId }

        return polls.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            val options = optionsByPollId[poll.id]?.map { PollOptionResponse.from(it) } ?: emptyList()
            PollListResponse.from(poll, responseCount, options)
        }
    }
}
