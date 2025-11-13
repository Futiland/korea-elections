package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PublicPollDraftCreateRequest
import com.futiland.vote.application.poll.dto.request.PollUpdateRequest
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.domain.poll.entity.*
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PollCommandService(
    private val pollRepository: PollRepository,
    private val pollOptionRepository: PollOptionRepository,
) : PollCommandUseCase {

    @Transactional
    override fun createPublicPoll(request: PublicPollCreateRequest, creatorAccountId: Long): PollDetailResponse {
        // 무조건 IN_PROGRESS로 생성, startAt은 현재 시간으로 자동 설정
        val poll = Poll.createActive(
            title = request.title,
            description = request.description,
            questionType = request.questionType,
            pollType = PollType.PUBLIC,
            allowMultipleResponses = request.allowMultipleResponses,
            creatorAccountId = creatorAccountId,
            endAt = request.endAt
        )

        val savedPoll = pollRepository.save(poll)

        // 점수제가 아닌 경우 옵션 생성
        val options = if (poll.questionType != QuestionType.SCORE && request.options != null) {
            val pollOptions = request.options.mapIndexed { index, optionReq ->
                PollOption.create(
                    pollId = savedPoll.id,
                    optionText = optionReq.optionText,
                    // optionOrder가 전달되지 않으면 리스트 순서대로 자동 부여 (1부터 시작)
                    optionOrder = optionReq.optionOrder ?: (index + 1)
                )
            }
            pollOptionRepository.saveAll(pollOptions)
        } else {
            emptyList()
        }

        return PollDetailResponse.from(savedPoll, options)
    }

    @Transactional
    override fun createPublicPollDraft(request: PublicPollDraftCreateRequest, creatorAccountId: Long): PollDetailResponse {
        // 무조건 DRAFT로 생성
        val poll = Poll.createDraft(
            title = request.title,
            description = request.description,
            questionType = request.questionType,
            pollType = PollType.PUBLIC,
            allowMultipleResponses = request.allowMultipleResponses,
            creatorAccountId = creatorAccountId
        )

        val savedPoll = pollRepository.save(poll)

        // 점수제가 아닌 경우 옵션 생성
        val options = if (poll.questionType != QuestionType.SCORE && request.options != null) {
            val pollOptions = request.options.mapIndexed { index, optionReq ->
                PollOption.create(
                    pollId = savedPoll.id,
                    optionText = optionReq.optionText,
                    // optionOrder가 전달되지 않으면 리스트 순서대로 자동 부여 (1부터 시작)
                    optionOrder = optionReq.optionOrder ?: (index + 1)
                )
            }
            pollOptionRepository.saveAll(pollOptions)
        } else {
            emptyList()
        }

        return PollDetailResponse.from(savedPoll, options)
    }

    @Transactional
    override fun updatePoll(pollId: Long, request: PollUpdateRequest): PollDetailResponse {
        val poll = pollRepository.getById(pollId)
        poll.update(
            title = request.title,
            description = request.description,
            startAt = request.startAt,
            endAt = request.endAt
        )
        val savedPoll = pollRepository.save(poll)
        val options = pollOptionRepository.findAllByPollId(pollId)
        return PollDetailResponse.from(savedPoll, options)
    }

    @Transactional
    override fun deletePoll(pollId: Long, accountId: Long) {
        val poll = pollRepository.getById(pollId)
        require(poll.creatorAccountId == accountId) { "본인이 만든 여론조사만 삭제할 수 있습니다" }
        poll.delete()
        pollRepository.save(poll)
    }

    @Transactional
    override fun cancelPoll(pollId: Long, accountId: Long) {
        val poll = pollRepository.getById(pollId)
        require(poll.creatorAccountId == accountId) { "본인이 만든 여론조사만 취소할 수 있습니다" }
        poll.cancel()
        pollRepository.save(poll)
    }
}
