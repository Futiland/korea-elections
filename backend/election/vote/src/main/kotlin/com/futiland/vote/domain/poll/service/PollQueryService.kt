package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
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
        val pollListResponses = pollsSlice.content.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            PollListResponse.from(poll, responseCount)
        }
        return SliceContent(pollListResponses, pollsSlice.nextCursor)
    }

    override fun getMyPolls(accountId: Long): List<PollListResponse> {
        val polls = pollRepository.findAllByCreatorAccountId(accountId)
        return polls.map { poll ->
            val responseCount = pollResponseRepository.countByPollId(poll.id)
            PollListResponse.from(poll, responseCount)
        }
    }
}
