package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PollOptionRequest
import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.application.poll.repository.FakePollOptionRepository
import com.futiland.vote.application.poll.repository.FakePollRepository
import com.futiland.vote.application.poll.repository.FakePollResponseOptionRepository
import com.futiland.vote.application.poll.repository.FakePollResponseRepository
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.entity.QuestionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class PollResponseCommandServiceTest {

    lateinit var pollCommandUseCase: PollCommandUseCase
    lateinit var pollResponseCommandUseCase: PollResponseCommandUseCase
    lateinit var pollRepository: FakePollRepository
    lateinit var pollOptionRepository: FakePollOptionRepository
    lateinit var pollResponseRepository: FakePollResponseRepository
    lateinit var pollResponseOptionRepository: FakePollResponseOptionRepository

    @BeforeEach
    fun setUp() {
        pollRepository = FakePollRepository()
        pollOptionRepository = FakePollOptionRepository()
        pollResponseRepository = FakePollResponseRepository()
        pollResponseOptionRepository = FakePollResponseOptionRepository()

        pollCommandUseCase = PollCommandService(
            pollRepository = pollRepository,
            pollOptionRepository = pollOptionRepository
        )

        pollResponseCommandUseCase = PollResponseCommandService(
            pollRepository = pollRepository,
            pollResponseRepository = pollResponseRepository,
            pollResponseOptionRepository = pollResponseOptionRepository
        )
    }

    @Nested
    inner class `단일 선택 응답` {
        @Test
        fun `단일 선택 응답 제출 성공`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    questionType = QuestionType.SINGLE_CHOICE,
                    allowMultipleResponses = false,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )

            // Act
            val responseId = pollResponseCommandUseCase.submitResponse(
                pollId = poll.id,
                accountId = 200L,
                request = request
            )

            // Assert
            assertThat(responseId).isGreaterThan(0)
            val savedResponse = pollResponseRepository.findById(responseId)
            assertThat(savedResponse).isNotNull
            assertThat(savedResponse?.pollId).isEqualTo(poll.id)
            assertThat(savedResponse?.accountId).isEqualTo(200L)

            val selectedOptions = pollResponseOptionRepository.findAllByResponseId(responseId)
            assertThat(selectedOptions).hasSize(1)
            assertThat(selectedOptions[0].optionId).isEqualTo(poll.options[0].id)
        }

        @Test
        fun `단일 선택에 여러 개 선택 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    questionType = QuestionType.SINGLE_CHOICE,
                    allowMultipleResponses = false,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.MultipleChoice(
                optionIds = listOf(poll.options[0].id, poll.options[1].id)
            )

            // Act & Assert
            assertThrows<IllegalArgumentException> {
                pollResponseCommandUseCase.submitResponse(
                    pollId = poll.id,
                    accountId = 200L,
                    request = request
                )
            }
        }

        @Test
        fun `중복 응답 불가 설정 시 중복 응답 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    questionType = QuestionType.SINGLE_CHOICE,
                    allowMultipleResponses = false,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )

            // 첫 번째 응답
            pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)

            // Act & Assert - 두 번째 응답 시도
            assertThrows<IllegalArgumentException> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
        }
    }

    @Nested
    inner class `다중 선택 응답` {
        @Test
        fun `다중 선택 응답 제출 성공`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는? (2-3개)",
                    description = "여러 개 선택",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    allowMultipleResponses = false,
                    minSelections = 2,
                    maxSelections = 3,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2),
                        PollOptionRequest("환경", 3),
                        PollOptionRequest("교육", 4)
                    )
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.MultipleChoice(
                optionIds = listOf(poll.options[0].id, poll.options[1].id)
            )

            // Act
            val responseId = pollResponseCommandUseCase.submitResponse(
                pollId = poll.id,
                accountId = 200L,
                request = request
            )

            // Assert
            val selectedOptions = pollResponseOptionRepository.findAllByResponseId(responseId)
            assertThat(selectedOptions).hasSize(2)
        }

        @Test
        fun `최소 선택 개수 미만 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는? (2-3개)",
                    description = "여러 개 선택",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    allowMultipleResponses = false,
                    minSelections = 2,
                    maxSelections = 3,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2)
                    )
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )

            // Act & Assert
            assertThrows<IllegalArgumentException> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
        }

        @Test
        fun `최대 선택 개수 초과 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는? (최대 2개)",
                    description = "여러 개 선택",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    allowMultipleResponses = false,
                    maxSelections = 2,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2),
                        PollOptionRequest("환경", 3)
                    )
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.MultipleChoice(
                optionIds = listOf(poll.options[0].id, poll.options[1].id, poll.options[2].id)
            )

            // Act & Assert
            assertThrows<IllegalArgumentException> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
        }
    }

    @Nested
    inner class `점수제 응답` {
        @Test
        fun `점수제 응답 제출 성공`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "정부 평가 점수",
                    description = "0-10점",
                    questionType = QuestionType.SCORE,
                    allowMultipleResponses = false,
                    minScore = 0,
                    maxScore = 10,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = null
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.Score(
                scoreValue = 7
            )

            // Act
            val responseId = pollResponseCommandUseCase.submitResponse(
                pollId = poll.id,
                accountId = 200L,
                request = request
            )

            // Assert
            val savedResponse = pollResponseRepository.findById(responseId)
            assertThat(savedResponse?.scoreValue).isEqualTo(7)
        }

        @Test
        fun `점수 범위 초과 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "정부 평가 점수",
                    description = "0-10점",
                    questionType = QuestionType.SCORE,
                    allowMultipleResponses = false,
                    minScore = 0,
                    maxScore = 10,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = null
                ),
                creatorAccountId = 100L
            )

            val request = PollResponseSubmitRequest.Score(
                scoreValue = 15
            )

            // Act & Assert
            assertThrows<IllegalArgumentException> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
        }
    }

    @Nested
    inner class `응답 수정` {
        @Test
        fun `재응답 허용 시 응답 수정 성공`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "수정 가능",
                    questionType = QuestionType.SINGLE_CHOICE,
                    allowMultipleResponses = true,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 100L
            )

            // 첫 번째 응답
            val firstRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )
            pollResponseCommandUseCase.submitResponse(poll.id, 200L, firstRequest)

            // 응답 수정
            val updateRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[1].id
            )

            // Act
            val responseId = pollResponseCommandUseCase.updateResponse(poll.id, 200L, updateRequest)

            // Assert
            val selectedOptions = pollResponseOptionRepository.findAllByResponseId(responseId)
            assertThat(selectedOptions).hasSize(1)
            assertThat(selectedOptions[0].optionId).isEqualTo(poll.options[1].id)
        }
    }
}
