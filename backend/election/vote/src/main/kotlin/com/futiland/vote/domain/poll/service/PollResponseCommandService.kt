package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.domain.poll.entity.PollResponse
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.deleteAll
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PollResponseCommandService(
    private val pollRepository: PollRepository,
    private val pollResponseRepository: PollResponseRepository,
) : PollResponseCommandUseCase {

    @Transactional
    override fun submitResponse(pollId: Long, accountId: Long, request: PollResponseSubmitRequest): Long {
        val poll = pollRepository.getById(pollId)

        // 진행 중인 여론조사만 응답 가능
        if (poll.status != PollStatus.IN_PROGRESS) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "진행 중인 여론조사만 응답 가능합니다"
            )
        }

        // 중복 응답 체크 및 재투표 처리
        val existingResponses = pollResponseRepository.findAllByPollIdAndAccountId(pollId, accountId)
        if (existingResponses.isNotEmpty()) {
            if (!poll.isRevotable) {
                throw ApplicationException(
                    code = CodeEnum.FRS_003,
                    message = "이미 응답한 여론조사입니다"
                )
            }
            existingResponses.deleteAll()
            pollResponseRepository.saveAll(existingResponses)
        }

        // 응답 유효성 검증
        poll.validateResponse(request)

        // 응답 저장
        val responses = when (request) {
            is PollResponseSubmitRequest.SingleChoice -> {
                listOf(
                    PollResponse.create(
                        pollId = pollId,
                        accountId = accountId,
                        optionId = request.optionId,
                        scoreValue = null
                    )
                )
            }
            is PollResponseSubmitRequest.MultipleChoice -> {
                request.optionIds.map { optionId ->
                    PollResponse.create(
                        pollId = pollId,
                        accountId = accountId,
                        optionId = optionId,
                        scoreValue = null
                    )
                }
            }
            is PollResponseSubmitRequest.Score -> {
                listOf(
                    PollResponse.create(
                        pollId = pollId,
                        accountId = accountId,
                        optionId = null,
                        scoreValue = request.scoreValue
                    )
                )
            }
        }

        val savedResponses = pollResponseRepository.saveAll(responses)
        return savedResponses.first().id
    }

    @Transactional
    override fun updateResponse(pollId: Long, accountId: Long, request: PollResponseSubmitRequest): Long {
        val poll = pollRepository.getById(pollId)

        // 진행 중인 여론조사만 수정 가능
        if (poll.status != PollStatus.IN_PROGRESS) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "진행 중인 여론조사만 수정 가능합니다"
            )
        }

        // 재응답 허용 여부 체크
        if (!poll.isRevotable) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "응답 수정이 허용되지 않는 여론조사입니다"
            )
        }

        // 기존 응답 조회 (다중 선택의 경우 여러 레코드 존재 가능)
        val existingResponses = pollResponseRepository.findAllByPollIdAndAccountId(pollId, accountId)
        if (existingResponses.isEmpty()) {
            throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "응답 내역이 없습니다"
            )
        }

        // 응답 유효성 검증
        poll.validateResponse(request)

        // 기존 응답 모두 삭제 (soft delete)
        existingResponses.deleteAll()
        pollResponseRepository.saveAll(existingResponses)

        // 새로운 응답 생성
        val responses = when (request) {
            is PollResponseSubmitRequest.SingleChoice -> {
                listOf(
                    PollResponse.create(
                        pollId = pollId,
                        accountId = accountId,
                        optionId = request.optionId,
                        scoreValue = null
                    )
                )
            }
            is PollResponseSubmitRequest.MultipleChoice -> {
                request.optionIds.map { optionId ->
                    PollResponse.create(
                        pollId = pollId,
                        accountId = accountId,
                        optionId = optionId,
                        scoreValue = null
                    )
                }
            }
            is PollResponseSubmitRequest.Score -> {
                listOf(
                    PollResponse.create(
                        pollId = pollId,
                        accountId = accountId,
                        optionId = null,
                        scoreValue = request.scoreValue
                    )
                )
            }
        }

        val savedResponses = pollResponseRepository.saveAll(responses)
        return savedResponses.first().id
    }

    @Transactional
    override fun deleteResponse(pollId: Long, accountId: Long) {
        val pollResponse = pollResponseRepository.findByPollIdAndAccountId(pollId, accountId)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "응답 내역이 없습니다"
            )

        pollResponse.delete()
        pollResponseRepository.save(pollResponse)
    }
}
