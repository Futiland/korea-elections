package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.domain.poll.entity.PollResponse
import com.futiland.vote.domain.poll.entity.PollResponseOption
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseOptionRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PollResponseCommandService(
    private val pollRepository: PollRepository,
    private val pollResponseRepository: PollResponseRepository,
    private val pollResponseOptionRepository: PollResponseOptionRepository,
) : PollResponseCommandUseCase {

    @Transactional
    override fun submitResponse(pollId: Long, accountId: Long, request: PollResponseSubmitRequest): Long {
        val poll = pollRepository.getById(pollId)

        // 진행 중인 여론조사만 응답 가능
        require(poll.status == PollStatus.IN_PROGRESS) { "진행 중인 여론조사만 응답 가능합니다" }

        // 중복 응답 체크
        if (!poll.allowMultipleResponses) {
            val existingResponse = pollResponseRepository.findByPollIdAndAccountId(pollId, accountId)
            require(existingResponse == null) { "이미 응답한 여론조사입니다" }
        }

        // 응답 유효성 검증
        poll.validateResponse(request.optionIds, request.scoreValue)

        // 응답 저장
        val pollResponse = PollResponse.create(
            pollId = pollId,
            accountId = accountId,
            scoreValue = request.scoreValue
        )
        val savedResponse = pollResponseRepository.save(pollResponse)

        // 선택지 응답 저장 (단일/다중 선택인 경우)
        if (request.optionIds != null) {
            val responseOptions = request.optionIds.map { optionId ->
                PollResponseOption.create(
                    responseId = savedResponse.id,
                    optionId = optionId
                )
            }
            pollResponseOptionRepository.saveAll(responseOptions)
        }

        return savedResponse.id
    }

    @Transactional
    override fun updateResponse(pollId: Long, accountId: Long, request: PollResponseSubmitRequest): Long {
        val poll = pollRepository.getById(pollId)

        // 진행 중인 여론조사만 수정 가능
        require(poll.status == PollStatus.IN_PROGRESS) { "진행 중인 여론조사만 수정 가능합니다" }

        // 재응답 허용 여부 체크
        require(poll.allowMultipleResponses) { "응답 수정이 허용되지 않는 여론조사입니다" }

        // 기존 응답 조회
        val existingResponse = pollResponseRepository.findByPollIdAndAccountId(pollId, accountId)
            ?: throw IllegalArgumentException("응답 내역이 없습니다")

        // 응답 유효성 검증
        poll.validateResponse(request.optionIds, request.scoreValue)

        // 점수 업데이트
        if (request.scoreValue != null) {
            existingResponse.updateScore(request.scoreValue)
        }

        // 선택지 업데이트 (기존 선택 삭제 후 재생성)
        if (request.optionIds != null) {
            pollResponseOptionRepository.deleteAllByResponseId(existingResponse.id)
            val responseOptions = request.optionIds.map { optionId ->
                PollResponseOption.create(
                    responseId = existingResponse.id,
                    optionId = optionId
                )
            }
            pollResponseOptionRepository.saveAll(responseOptions)
        }

        pollResponseRepository.save(existingResponse)
        return existingResponse.id
    }

    @Transactional
    override fun deleteResponse(pollId: Long, accountId: Long) {
        val pollResponse = pollResponseRepository.findByPollIdAndAccountId(pollId, accountId)
            ?: throw IllegalArgumentException("응답 내역이 없습니다")

        pollResponse.delete()
        pollResponseRepository.save(pollResponse)
    }
}
