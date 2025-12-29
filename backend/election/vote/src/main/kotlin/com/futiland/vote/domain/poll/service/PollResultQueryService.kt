package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.poll.dto.response.MyPollResponse
import com.futiland.vote.application.poll.dto.response.OptionResultResponse
import com.futiland.vote.application.poll.dto.response.PollResultResponse
import com.futiland.vote.application.poll.dto.response.ScoreResultResponse
import com.futiland.vote.domain.poll.entity.ResponseType
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import org.springframework.stereotype.Service

// TODO: Poll 엔티티에 minScore, maxScore 필드 추가 후 DB 마이그레이션하여 투표별 점수 범위 지정 가능하도록 변경
private const val MIN_SCORE = 0
private const val MAX_SCORE = 10

@Service
class PollResultQueryService(
    private val pollRepository: PollRepository,
    private val pollOptionRepository: PollOptionRepository,
    private val pollResponseRepository: PollResponseRepository,
) : PollResultQueryUseCase {

    override fun getPollResult(pollId: Long, accountId: Long): PollResultResponse {
        val poll = pollRepository.getById(pollId)
        val totalResponseCount = pollResponseRepository.countDistinctParticipantsByPollId(pollId)

        val myResponse = getMyResponse(pollId, accountId, poll.responseType)

        return when (poll.responseType) {
            ResponseType.SINGLE_CHOICE, ResponseType.MULTIPLE_CHOICE -> {
                val options = pollOptionRepository.findAllByPollId(pollId)
                val optionResults = options.map { option ->
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
                    scoreResult = null,
                    myResponse = myResponse
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

                val scoreDistribution = (MIN_SCORE..MAX_SCORE).associateWith { score ->
                    scores.count { it == score }.toLong()
                }

                val scoreResult = ScoreResultResponse(
                    averageScore = averageScore,
                    minScore = MIN_SCORE,
                    maxScore = MAX_SCORE,
                    scoreDistribution = scoreDistribution
                )

                PollResultResponse(
                    pollId = pollId,
                    responseType = poll.responseType,
                    totalResponseCount = totalResponseCount,
                    optionResults = null,
                    scoreResult = scoreResult,
                    myResponse = myResponse
                )
            }
        }
    }

    private fun getMyResponse(pollId: Long, accountId: Long, responseType: ResponseType): MyPollResponse {
        val myResponses = pollResponseRepository.findAllByPollIdAndAccountId(pollId, accountId)
        if (myResponses.isEmpty()) {
            throw ApplicationException(
                code = CodeEnum.FRS_002,
                message = "투표에 참여한 사용자만 결과를 볼 수 있습니다"
            )
        }

        val createdAt = myResponses.maxOf { it.createdAt }
        val updatedAt = myResponses.mapNotNull { it.updatedAt }.maxOrNull()

        return when (responseType) {
            ResponseType.SINGLE_CHOICE -> MyPollResponse.SingleChoice(
                selectedOptionId = myResponses.first().optionId!!,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
            ResponseType.MULTIPLE_CHOICE -> MyPollResponse.MultipleChoice(
                selectedOptionIds = myResponses.mapNotNull { it.optionId },
                createdAt = createdAt,
                updatedAt = updatedAt
            )
            ResponseType.SCORE -> MyPollResponse.Score(
                scoreValue = myResponses.first().scoreValue!!,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
