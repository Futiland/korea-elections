package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.account.repository.FakeAccountRepository
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PollOptionRequest
import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.application.poll.repository.FakePollOptionRepository
import com.futiland.vote.application.poll.repository.FakePollRepository
import com.futiland.vote.application.poll.repository.FakePollResponseRepository
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.poll.entity.ResponseType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime

class PollResponseCommandServiceTest {

    lateinit var pollCommandUseCase: PollCommandUseCase
    lateinit var pollResponseCommandUseCase: PollResponseCommandUseCase
    lateinit var pollRepository: FakePollRepository
    lateinit var pollOptionRepository: FakePollOptionRepository
    lateinit var pollResponseRepository: FakePollResponseRepository
    lateinit var accountRepository: FakeAccountRepository

    @BeforeEach
    fun setUp() {
        pollRepository = FakePollRepository()
        pollOptionRepository = FakePollOptionRepository()
        pollResponseRepository = FakePollResponseRepository()
        accountRepository = FakeAccountRepository()

        // 테스트용 계정 생성
        val testAccount = Account.create(
            name = "테스트유저",
            phoneNumber = "01012345678",
            password = "password",
            gender = Gender.MALE,
            birthDate = LocalDate.of(1990, 1, 1),
            ci = "test-ci"
        )
        accountRepository.save(testAccount)

        pollCommandUseCase = PollCommandService(
            pollRepository = pollRepository,
            pollOptionRepository = pollOptionRepository,
            accountRepository = accountRepository
        )

        pollResponseCommandUseCase = PollResponseCommandService(
            pollRepository = pollRepository,
            pollResponseRepository = pollResponseRepository,
            pollOptionRepository = pollOptionRepository
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
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
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
            assertThat(savedResponse?.optionId).isEqualTo(poll.options[0].id)
        }

        @Test
        fun `단일 선택에 여러 개 선택 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
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
        fun `재투표 불가 설정 시 중복 응답 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            val request = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )

            // 첫 번째 응답
            pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)

            // Act & Assert - 두 번째 응답 시도
            assertThrows<Exception> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
        }

        @Test
        fun `재투표 허용 시 재응답 성공`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = true,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            val firstRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )

            // 첫 번째 응답
            pollResponseCommandUseCase.submitResponse(poll.id, 200L, firstRequest)

            // 두 번째 응답 (재투표)
            val secondRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[1].id
            )

            // Act
            val responseId = pollResponseCommandUseCase.submitResponse(poll.id, 200L, secondRequest)

            // Assert
            assertThat(responseId).isGreaterThan(0)
            val savedResponse = pollResponseRepository.findById(responseId)
            assertThat(savedResponse?.optionId).isEqualTo(poll.options[1].id)
        }
    }

    @Nested
    inner class `다중 선택 응답` {
        @Test
        fun `다중 선택 응답 제출 성공`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는?",
                    description = "여러 개 선택",
                    responseType = ResponseType.MULTIPLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2),
                        PollOptionRequest("환경", 3),
                        PollOptionRequest("교육", 4)
                    )
                ),
                creatorAccountId = 1L
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
            assertThat(responseId).isGreaterThan(0)
            val responses = pollResponseRepository.findAllByPollIdAndAccountId(poll.id, 200L)
            assertThat(responses).hasSize(2)
        }

        @Test
        fun `다중 선택에 단일 선택 요청 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는?",
                    description = "여러 개 선택",
                    responseType = ResponseType.MULTIPLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            val request = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
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
                    responseType = ResponseType.SCORE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = null
                ),
                creatorAccountId = 1L
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
                    responseType = ResponseType.SCORE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = null
                ),
                creatorAccountId = 1L
            )

            val request = PollResponseSubmitRequest.Score(
                scoreValue = 15
            )

            // Act & Assert
            val exception = assertThrows<ApplicationException> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
            assertThat(exception.code).isEqualTo(CodeEnum.FRS_003)
            assertThat(exception.message).contains("점수는 0~10 사이여야 합니다")
        }

        @Test
        fun `점수 범위 미만 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "정부 평가 점수",
                    description = "0-10점",
                    responseType = ResponseType.SCORE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = null
                ),
                creatorAccountId = 1L
            )

            val request = PollResponseSubmitRequest.Score(
                scoreValue = -1
            )

            // Act & Assert
            val exception = assertThrows<ApplicationException> {
                pollResponseCommandUseCase.submitResponse(poll.id, 200L, request)
            }
            assertThat(exception.code).isEqualTo(CodeEnum.FRS_003)
            assertThat(exception.message).contains("점수는 0~10 사이여야 합니다")
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
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = true,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
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
            val savedResponse = pollResponseRepository.findById(responseId)
            assertThat(savedResponse?.optionId).isEqualTo(poll.options[1].id)
        }

        @Test
        fun `재응답 불허용 시 응답 수정 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "수정 불가",
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            // 첫 번째 응답
            val firstRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )
            pollResponseCommandUseCase.submitResponse(poll.id, 200L, firstRequest)

            // 응답 수정 시도
            val updateRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[1].id
            )

            // Act & Assert
            assertThrows<Exception> {
                pollResponseCommandUseCase.updateResponse(poll.id, 200L, updateRequest)
            }
        }
    }

    @Nested
    inner class `존재하지 않는 옵션 선택` {

        @Test
        fun `단일 선택 - 존재하지 않는 optionId로 응답 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "하나만 선택",
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            val nonExistentOptionId = 99999L
            val request = PollResponseSubmitRequest.SingleChoice(
                optionId = nonExistentOptionId
            )

            // Act & Assert
            val exception = assertThrows<ApplicationException> {
                pollResponseCommandUseCase.submitResponse(
                    pollId = poll.id,
                    accountId = 200L,
                    request = request
                )
            }
            assertThat(exception.code).isEqualTo(CodeEnum.FRS_003)
            assertThat(exception.message).contains("존재하지 않는 옵션")
        }

        @Test
        fun `다중 선택 - 일부 optionId가 존재하지 않을 때 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는?",
                    description = "여러 개 선택",
                    responseType = ResponseType.MULTIPLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            val nonExistentOptionId = 99999L
            val request = PollResponseSubmitRequest.MultipleChoice(
                optionIds = listOf(poll.options[0].id, nonExistentOptionId)
            )

            // Act & Assert
            val exception = assertThrows<ApplicationException> {
                pollResponseCommandUseCase.submitResponse(
                    pollId = poll.id,
                    accountId = 200L,
                    request = request
                )
            }
            assertThat(exception.code).isEqualTo(CodeEnum.FRS_003)
            assertThat(exception.message).contains("존재하지 않는 옵션")
        }

        @Test
        fun `다중 선택 - 모든 optionId가 존재하지 않을 때 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "관심 분야는?",
                    description = "여러 개 선택",
                    responseType = ResponseType.MULTIPLE_CHOICE,
                    isRevotable = false,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("경제", 1),
                        PollOptionRequest("외교", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            val request = PollResponseSubmitRequest.MultipleChoice(
                optionIds = listOf(99998L, 99999L)
            )

            // Act & Assert
            val exception = assertThrows<ApplicationException> {
                pollResponseCommandUseCase.submitResponse(
                    pollId = poll.id,
                    accountId = 200L,
                    request = request
                )
            }
            assertThat(exception.code).isEqualTo(CodeEnum.FRS_003)
        }

        @Test
        fun `응답 수정 - 존재하지 않는 optionId로 수정 시 실패`() {
            // Arrange
            val poll = pollCommandUseCase.createPublicPoll(
                PublicPollCreateRequest(
                    title = "좋아하는 과일은?",
                    description = "수정 가능",
                    responseType = ResponseType.SINGLE_CHOICE,
                    isRevotable = true,
                    endAt = LocalDateTime.now().plusDays(7),
                    options = listOf(
                        PollOptionRequest("사과", 1),
                        PollOptionRequest("바나나", 2)
                    )
                ),
                creatorAccountId = 1L
            )

            // 먼저 유효한 응답 제출
            val validRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = poll.options[0].id
            )
            pollResponseCommandUseCase.submitResponse(poll.id, 200L, validRequest)

            // 존재하지 않는 optionId로 수정 시도
            val invalidRequest = PollResponseSubmitRequest.SingleChoice(
                optionId = 99999L
            )

            // Act & Assert
            val exception = assertThrows<ApplicationException> {
                pollResponseCommandUseCase.updateResponse(
                    pollId = poll.id,
                    accountId = 200L,
                    request = invalidRequest
                )
            }
            assertThat(exception.code).isEqualTo(CodeEnum.FRS_003)
        }
    }
}
