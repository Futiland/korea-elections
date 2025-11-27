package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PublicPollDraftCreateRequest
import com.futiland.vote.application.poll.dto.request.PollOptionRequest
import com.futiland.vote.application.poll.dto.request.PollUpdateRequest
import com.futiland.vote.application.poll.repository.FakePollOptionRepository
import com.futiland.vote.application.poll.repository.FakePollRepository
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.entity.QuestionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class PollCommandServiceTest {

    lateinit var pollCommandUseCase: PollCommandUseCase
    lateinit var pollRepository: FakePollRepository
    lateinit var pollOptionRepository: FakePollOptionRepository

    @BeforeEach
    fun setUp() {
        pollRepository = FakePollRepository()
        pollOptionRepository = FakePollOptionRepository()
        pollCommandUseCase = PollCommandService(
            pollRepository = pollRepository,
            pollOptionRepository = pollOptionRepository
        )
    }

    @Nested
    inner class `여론조사 생성` {
        @Test
        fun `단일 선택 여론조사 생성 성공`() {
            // Arrange
            val request = PublicPollCreateRequest(
                title = "좋아하는 과일은?",
                description = "가장 좋아하는 과일을 선택하세요",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(
                    PollOptionRequest("사과", 1),
                    PollOptionRequest("바나나", 2),
                    PollOptionRequest("오렌지", 3)
                )
            )

            // Act
            val response = pollCommandUseCase.createPublicPoll(request, creatorAccountId = 100L)

            // Assert
            assertThat(response.id).isGreaterThan(0)
            assertThat(response.title).isEqualTo("좋아하는 과일은?")
            assertThat(response.questionType).isEqualTo(QuestionType.SINGLE_CHOICE)
            assertThat(response.options).hasSize(3)
            assertThat(response.options[0].optionText).isEqualTo("사과")
        }

        @Test
        fun `다중 선택 여론조사 생성 성공`() {
            // Arrange
            val request = PublicPollCreateRequest(
                title = "관심 분야는? (2-3개 선택)",
                description = "관심 있는 분야를 선택하세요",
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
            )

            // Act
            val response = pollCommandUseCase.createPublicPoll(request, creatorAccountId = 100L)

            // Assert
            assertThat(response.questionType).isEqualTo(QuestionType.MULTIPLE_CHOICE)
            assertThat(response.minSelections).isEqualTo(2)
            assertThat(response.maxSelections).isEqualTo(3)
            assertThat(response.options).hasSize(4)
        }

        @Test
        fun `점수제 여론조사 생성 성공`() {
            // Arrange
            val request = PublicPollCreateRequest(
                title = "정부 평가 점수는?",
                description = "0~10점으로 평가하세요",
                questionType = QuestionType.SCORE,
                allowMultipleResponses = false,
                minScore = 0,
                maxScore = 10,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7),
                options = null
            )

            // Act
            val response = pollCommandUseCase.createPublicPoll(request, creatorAccountId = 100L)

            // Assert
            assertThat(response.questionType).isEqualTo(QuestionType.SCORE)
            assertThat(response.minScore).isEqualTo(0)
            assertThat(response.maxScore).isEqualTo(10)
            assertThat(response.options).isEmpty()
        }

        @Test
        fun `DRAFT 상태로 생성 성공`() {
            // Arrange
            val request = PublicPollDraftCreateRequest(
                title = "좋아하는 색은?",
                description = "작성 중",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                options = listOf(
                    PollOptionRequest("빨강", 1),
                    PollOptionRequest("파랑", 2)
                )
            )

            // Act
            val response = pollCommandUseCase.createPublicPollDraft(request, creatorAccountId = 100L)

            // Assert
            assertThat(response.status.name).isEqualTo("DRAFT")
            assertThat(response.startAt).isNull()
            assertThat(response.endAt).isNull()
        }
    }

    @Nested
    inner class `여론조사 수정` {
        @Test
        fun `DRAFT 상태 여론조사 수정 성공`() {
            // Arrange
            val createRequest = PublicPollDraftCreateRequest(
                title = "원래 제목",
                description = "원래 설명",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPollDraft(createRequest, 100L)

            val updateRequest = PollUpdateRequest(
                title = "수정된 제목",
                description = "수정된 설명",
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7)
            )

            // Act
            val response = pollCommandUseCase.updatePoll(created.id, updateRequest)

            // Assert
            assertThat(response.title).isEqualTo("수정된 제목")
            assertThat(response.description).isEqualTo("수정된 설명")
            assertThat(response.startAt).isNotNull()
        }

        @Test
        fun `진행중인 여론조사 수정 실패`() {
            // Arrange
            val createRequest = PublicPollCreateRequest(
                title = "진행중인 투표",
                description = "설명",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 100L)

            val updateRequest = PollUpdateRequest(title = "수정 시도")

            // Act & Assert
            assertThrows<IllegalArgumentException> {
                pollCommandUseCase.updatePoll(created.id, updateRequest)
            }
        }
    }

    @Nested
    inner class `여론조사 삭제` {
        @Test
        fun `본인이 만든 여론조사 삭제 성공`() {
            // Arrange
            val createRequest = PublicPollCreateRequest(
                title = "삭제될 투표",
                description = "설명",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 100L)

            // Act
            pollCommandUseCase.deletePoll(created.id, accountId = 100L)

            // Assert
            val poll = pollRepository.findById(created.id)
            assertThat(poll?.deletedAt).isNotNull()
        }

        @Test
        fun `다른 사람이 만든 여론조사 삭제 실패`() {
            // Arrange
            val createRequest = PublicPollCreateRequest(
                title = "삭제될 투표",
                description = "설명",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 100L)

            // Act & Assert
            assertThrows<IllegalArgumentException> {
                pollCommandUseCase.deletePoll(created.id, accountId = 999L)
            }
        }
    }

    @Nested
    inner class `여론조사 취소` {
        @Test
        fun `진행중인 여론조사 취소 성공`() {
            // Arrange
            val createRequest = PublicPollCreateRequest(
                title = "취소될 투표",
                description = "설명",
                questionType = QuestionType.SINGLE_CHOICE,
                allowMultipleResponses = false,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 100L)

            // Act
            pollCommandUseCase.cancelPoll(created.id, accountId = 100L)

            // Assert
            val poll = pollRepository.findById(created.id)
            assertThat(poll?.status?.name).isEqualTo("CANCELLED")
        }
    }
}
