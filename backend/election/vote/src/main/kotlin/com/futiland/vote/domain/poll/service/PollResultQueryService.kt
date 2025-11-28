package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.poll.dto.response.OptionResultResponse
import com.futiland.vote.application.poll.dto.response.PollResultResponse
import com.futiland.vote.application.poll.dto.response.ScoreResultResponse
import com.futiland.vote.domain.poll.entity.ResponseType
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import org.springframework.stereotype.Service

@Service
class PollResultQueryService(
    private val pollRepository: PollRepository,
    private val pollOptionRepository: PollOptionRepository,
    private val pollResponseRepository: PollResponseRepository,
) : PollResultQueryUseCase {

    override fun getPollResult(pollId: Long, accountId: Long): PollResultResponse {
        validateParticipation(pollId, accountId)

        val poll = pollRepository.getById(pollId)
        val totalResponseCount = pollResponseRepository.countByPollId(pollId)

        return when (poll.responseType) {
            ResponseType.SINGLE_CHOICE, ResponseType.MULTIPLE_CHOICE -> {
                val options = pollOptionRepository.findAllByPollId(pollId)
                val optionResults = options.map { option ->
                    // PollResponse의 optionId로 직접 카운트
                    val voteCount = pollResponseRepository.countByOptionId(option.id)
                    val percentage = if (totalResponseCount > 0) {
                        (voteCount.toDouble() / totalResponseCount.toDouble()) * 100
                    } else {
                        0.0
                    }
                    OptionResultResponse(
                        optionId = option.id,
                        optionText = option.optionText,
                        voteCount = voteCount,
                        percentage = percentage
                    )
                }
                PollResultResponse(
                    pollId = pollId,
                    responseType = poll.responseType,
                    totalResponseCount = totalResponseCount,
                    optionResults = optionResults,
                    scoreResult = null
                )
            }
            ResponseType.SCORE -> {
                val responses = pollResponseRepository.findAllByPollId(pollId)
                val scores = responses.mapNotNull { it.scoreValue }

                val averageScore = if (scores.isNotEmpty()) {
                    scores.average()
                } else {
                    0.0
                }

                val scoreDistribution = scores.groupingBy { it }.eachCount().mapValues { it.value.toLong() }

                val scoreResult = ScoreResultResponse(
                    averageScore = averageScore,
                    minScore = 0, // 점수제 기본 최소값
                    maxScore = 10, // 점수제 기본 최대값
                    scoreDistribution = scoreDistribution
                )

                PollResultResponse(
                    pollId = pollId,
                    responseType = poll.responseType,
                    totalResponseCount = totalResponseCount,
                    optionResults = null,
                    scoreResult = scoreResult
                )
            }
        }
    }

    // TODO 중복된 조회가 있는것으로 보이므로 추후 수정하기
    private fun validateParticipation(pollId: Long, accountId: Long) {
        val response = pollResponseRepository.findByPollIdAndAccountId(pollId, accountId) ?: throw ApplicationException(
            code = CodeEnum.FRS_002,
            message = "투표에 참여한 사용자만 결과를 볼 수 있습니다"
        )
    }
}
