package com.futiland.vote.domain.poll.service

import com.futiland.vote.application.account.repository.FakeAccountRepository
import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PublicPollDraftCreateRequest
import com.futiland.vote.application.poll.dto.request.PollOptionRequest
import com.futiland.vote.application.poll.dto.request.PollUpdateRequest
import com.futiland.vote.application.poll.repository.FakePollOptionRepository
import com.futiland.vote.application.poll.repository.FakePollRepository
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

class PollCommandServiceTest {

    lateinit var pollCommandUseCase: PollCommandUseCase
    lateinit var pollRepository: FakePollRepository
    lateinit var pollOptionRepository: FakePollOptionRepository
    lateinit var accountRepository: FakeAccountRepository

    @BeforeEach
    fun setUp() {
        pollRepository = FakePollRepository()
        pollOptionRepository = FakePollOptionRepository()
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
    }

    @Nested
    inner class `여론조사 생성` {
        @Test
        fun `단일 선택 여론조사 생성 성공`() {
            // Arrange
            val request = PublicPollCreateRequest(
                title = "좋아하는 과일은?",
                description = "가장 좋아하는 과일을 선택하세요",
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(
                    PollOptionRequest("사과", 1),
                    PollOptionRequest("바나나", 2),
                    PollOptionRequest("오렌지", 3)
                )
            )

            // Act
            val response = pollCommandUseCase.createPublicPoll(request, creatorAccountId = 1L)

            // Assert
            assertThat(response.id).isGreaterThan(0)
            assertThat(response.title).isEqualTo("좋아하는 과일은?")
            assertThat(response.responseType).isEqualTo(ResponseType.SINGLE_CHOICE)
            assertThat(response.options).hasSize(3)
            assertThat(response.options[0].optionText).isEqualTo("사과")
        }

        @Test
        fun `다중 선택 여론조사 생성 성공`() {
            // Arrange
            val request = PublicPollCreateRequest(
                title = "관심 분야는?",
                description = "관심 있는 분야를 선택하세요",
                responseType = ResponseType.MULTIPLE_CHOICE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(
                    PollOptionRequest("경제", 1),
                    PollOptionRequest("외교", 2),
                    PollOptionRequest("환경", 3),
                    PollOptionRequest("교육", 4)
                )
            )

            // Act
            val response = pollCommandUseCase.createPublicPoll(request, creatorAccountId = 1L)

            // Assert
            assertThat(response.responseType).isEqualTo(ResponseType.MULTIPLE_CHOICE)
            assertThat(response.options).hasSize(4)
        }

        @Test
        fun `점수제 여론조사 생성 성공`() {
            // Arrange
            val request = PublicPollCreateRequest(
                title = "정부 평가 점수는?",
                description = "0~10점으로 평가하세요",
                responseType = ResponseType.SCORE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = null
            )

            // Act
            val response = pollCommandUseCase.createPublicPoll(request, creatorAccountId = 1L)

            // Assert
            assertThat(response.responseType).isEqualTo(ResponseType.SCORE)
            assertThat(response.options).isEmpty()
        }

        @Test
        fun `DRAFT 상태로 생성 성공`() {
            // Arrange
            val request = PublicPollDraftCreateRequest(
                title = "좋아하는 색은?",
                description = "작성 중",
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                options = listOf(
                    PollOptionRequest("빨강", 1),
                    PollOptionRequest("파랑", 2)
                )
            )

            // Act
            val response = pollCommandUseCase.createPublicPollDraft(request, creatorAccountId = 1L)

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
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPollDraft(createRequest, 1L)

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
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 1L)

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
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 1L)

            // Act
            pollCommandUseCase.deletePoll(created.id, accountId = 1L)

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
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 1L)

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
                responseType = ResponseType.SINGLE_CHOICE,
                isRevotable = false,
                endAt = LocalDateTime.now().plusDays(7),
                options = listOf(PollOptionRequest("옵션1", 1))
            )
            val created = pollCommandUseCase.createPublicPoll(createRequest, 1L)

            // Act
            pollCommandUseCase.cancelPoll(created.id, accountId = 1L)

            // Assert
            val poll = pollRepository.findById(created.id)
            assertThat(poll?.status?.name).isEqualTo("CANCELLED")
        }
    }
}
