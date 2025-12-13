package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.config.security.CustomUserDto
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollOption
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.entity.ResponseType
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("h2-test")
@Transactional
class PollQueryControllerIntegrationTest {

    @Autowired
    lateinit var pollQueryController: PollQueryController

    @Autowired
    lateinit var pollRepository: PollRepository

    @Autowired
    lateinit var pollOptionRepository: PollOptionRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    private lateinit var testAccount: Account

    @BeforeEach
    fun setUp() {
        testAccount = accountRepository.save(
            Account.create(
                name = "테스트유저",
                phoneNumber = "01012345678",
                password = "password123",
                gender = Gender.MALE,
                birthDate = LocalDate.of(1990, 1, 1),
                ci = "test-ci-${System.currentTimeMillis()}"
            )
        )
    }

    private fun createTestPoll(
        title: String = "테스트 여론조사",
        description: String = "테스트 설명",
        status: PollStatus = PollStatus.IN_PROGRESS,
        creatorAccountId: Long = testAccount.id,
        isRevotable: Boolean = true,
        endAt: LocalDateTime = LocalDateTime.now().plusDays(7)
    ): Poll {
        val poll = if (status == PollStatus.DRAFT) {
            Poll.createDraft(
                title = title,
                description = description,
                responseType = ResponseType.SINGLE_CHOICE,
                pollType = PollType.PUBLIC,
                isRevotable = isRevotable,
                creatorAccountId = creatorAccountId
            )
        } else {
            Poll.createActive(
                title = title,
                description = description,
                responseType = ResponseType.SINGLE_CHOICE,
                pollType = PollType.PUBLIC,
                isRevotable = isRevotable,
                creatorAccountId = creatorAccountId,
                endAt = endAt
            )
        }
        return pollRepository.save(poll)
    }

    private fun createTestPollOptions(pollId: Long): List<PollOption> {
        val options = listOf(
            PollOption.create(pollId = pollId, optionText = "옵션1", optionOrder = 1),
            PollOption.create(pollId = pollId, optionText = "옵션2", optionOrder = 2),
            PollOption.create(pollId = pollId, optionText = "옵션3", optionOrder = 3)
        )
        return pollOptionRepository.saveAll(options)
    }

    private fun createUserDetails(accountId: Long): CustomUserDetails {
        return CustomUserDetails(
            user = CustomUserDto(accountId = accountId)
        )
    }

    @Nested
    inner class `공개 여론조사 목록 조회` {

        @Test
        fun `공개 여론조사 목록 조회 성공`() {
            // Arrange
            val poll1 = createTestPoll(title = "여론조사1")
            createTestPollOptions(poll1.id)
            val poll2 = createTestPoll(title = "여론조사2")
            createTestPollOptions(poll2.id)

            // Act
            val response = pollQueryController.getPublicPollList(
                size = 10,
                nextCursor = null,
                userDetails = null
            )

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.content).isNotEmpty
        }

        @Test
        fun `로그인한 사용자가 공개 여론조사 목록 조회 성공`() {
            // Arrange
            val poll = createTestPoll(title = "로그인 사용자 조회 테스트")
            createTestPollOptions(poll.id)

            // Act
            val response = pollQueryController.getPublicPollList(
                size = 10,
                nextCursor = null,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.content).isNotEmpty
        }

        @Test
        fun `DRAFT 상태의 여론조사는 공개 목록에서 조회되지 않는다`() {
            // Arrange
            val draftPoll = createTestPoll(title = "작성중인 여론조사", status = PollStatus.DRAFT)
            createTestPollOptions(draftPoll.id)

            // Act
            val response = pollQueryController.getPublicPollList(
                size = 10,
                nextCursor = null,
                userDetails = null
            )

            // Assert
            assertThat(response.data).isNotNull
            val pollIds = response.data?.content?.map { it.id } ?: emptyList()
            assertThat(pollIds).doesNotContain(draftPoll.id)
        }

        @Test
        fun `페이지 사이즈를 지정하여 조회 성공`() {
            // Arrange
            repeat(5) { index ->
                val poll = createTestPoll(title = "여론조사 $index")
                createTestPollOptions(poll.id)
            }

            // Act
            val response = pollQueryController.getPublicPollList(
                size = 2,
                nextCursor = null,
                userDetails = null
            )

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.content?.size).isLessThanOrEqualTo(2)
        }

        @Test
        fun `커서 기반 페이지네이션으로 다음 페이지 조회 성공`() {
            // Arrange
            repeat(5) { index ->
                val poll = createTestPoll(title = "페이지네이션 테스트 $index")
                createTestPollOptions(poll.id)
            }

            // Act - 첫 페이지 조회
            val firstPageResponse = pollQueryController.getPublicPollList(
                size = 2,
                nextCursor = null,
                userDetails = null
            )

            val nextCursor = firstPageResponse.data?.nextCursor

            // Assert - 첫 페이지
            assertThat(firstPageResponse.data).isNotNull
            assertThat(firstPageResponse.data?.content?.size).isLessThanOrEqualTo(2)

            // Act - 두 번째 페이지 조회 (nextCursor가 있는 경우)
            if (nextCursor != null) {
                val secondPageResponse = pollQueryController.getPublicPollList(
                    size = 2,
                    nextCursor = nextCursor,
                    userDetails = null
                )

                // Assert - 두 번째 페이지
                assertThat(secondPageResponse.data).isNotNull
            }
        }
    }

    @Nested
    inner class `여론조사 상세 조회` {

        @Test
        fun `여론조사 상세 조회 성공`() {
            // Arrange
            val poll = createTestPoll(title = "상세 조회 테스트", description = "상세 설명입니다")
            createTestPollOptions(poll.id)

            // Act
            val response = pollQueryController.getPollDetail(pollId = poll.id, userDetails = null)

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.id).isEqualTo(poll.id)
            assertThat(response.data?.title).isEqualTo("상세 조회 테스트")
            assertThat(response.data?.description).isEqualTo("상세 설명입니다")
            assertThat(response.data?.options).hasSize(3)
        }

        @Test
        fun `여론조사 상세 조회 시 옵션 정보가 포함된다`() {
            // Arrange
            val poll = createTestPoll(title = "옵션 테스트")
            createTestPollOptions(poll.id)

            // Act
            val response = pollQueryController.getPollDetail(pollId = poll.id, userDetails = null)

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.options).isNotEmpty
            assertThat(response.data?.options?.first()?.optionText).isEqualTo("옵션1")
            assertThat(response.data?.options?.first()?.optionOrder).isEqualTo(1)
        }

        @Test
        fun `여론조사 상세 조회 시 작성자 정보가 포함된다`() {
            // Arrange
            val poll = createTestPoll(title = "작성자 정보 테스트", creatorAccountId = testAccount.id)
            createTestPollOptions(poll.id)

            // Act
            val response = pollQueryController.getPollDetail(pollId = poll.id, userDetails = null)

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.creatorInfo).isNotNull
            assertThat(response.data?.creatorInfo?.accountId).isEqualTo(testAccount.id)
        }

        @Test
        fun `존재하지 않는 여론조사 조회 실패`() {
            // Act & Assert
            assertThrows<Exception> {
                pollQueryController.getPollDetail(pollId = 999999L, userDetails = null)
            }
        }
    }

    @Nested
    inner class `내가 만든 여론조사 목록 조회` {

        @Test
        fun `내가 만든 여론조사 목록 조회 성공`() {
            // Arrange
            val poll1 = createTestPoll(title = "내 여론조사1", creatorAccountId = testAccount.id)
            createTestPollOptions(poll1.id)
            val poll2 = createTestPoll(title = "내 여론조사2", creatorAccountId = testAccount.id)
            createTestPollOptions(poll2.id)

            // Act
            val response = pollQueryController.getMyPolls(
                page = 1,
                size = 10,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.content).isNotEmpty
            response.data?.content?.forEach { poll ->
                assertThat(poll.creatorInfo.accountId).isEqualTo(testAccount.id)
            }
        }

        @Test
        fun `다른 사용자의 여론조사는 내 목록에서 조회되지 않는다`() {
            // Arrange
            val otherAccount = accountRepository.save(
                Account.create(
                    name = "다른유저",
                    phoneNumber = "01087654321",
                    password = "password456",
                    gender = Gender.FEMALE,
                    birthDate = LocalDate.of(1995, 5, 5),
                    ci = "other-ci-${System.currentTimeMillis()}"
                )
            )
            val otherUserPoll = createTestPoll(title = "다른 사용자의 여론조사", creatorAccountId = otherAccount.id)
            createTestPollOptions(otherUserPoll.id)

            // Act
            val response = pollQueryController.getMyPolls(
                page = 1,
                size = 10,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert
            assertThat(response.data).isNotNull
            val pollIds = response.data?.content?.map { it.id } ?: emptyList()
            assertThat(pollIds).doesNotContain(otherUserPoll.id)
        }

        @Test
        fun `DRAFT 상태의 여론조사도 내 목록에서 조회된다`() {
            // Arrange
            val draftPoll = createTestPoll(
                title = "작성중인 내 여론조사",
                status = PollStatus.DRAFT,
                creatorAccountId = testAccount.id
            )
            createTestPollOptions(draftPoll.id)

            // Act
            val response = pollQueryController.getMyPolls(
                page = 1,
                size = 10,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert
            assertThat(response.data).isNotNull
            val pollIds = response.data?.content?.map { it.id } ?: emptyList()
            assertThat(pollIds).contains(draftPoll.id)
        }

        @Test
        fun `페이지 사이즈를 지정하여 내 여론조사 조회 성공`() {
            // Arrange
            repeat(5) { index ->
                val poll = createTestPoll(title = "내 여론조사 $index", creatorAccountId = testAccount.id)
                createTestPollOptions(poll.id)
            }

            // Act
            val response = pollQueryController.getMyPolls(
                page = 1,
                size = 2,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.content?.size).isLessThanOrEqualTo(2)
        }

        @Test
        fun `페이지 기반 페이지네이션으로 내 여론조사 다음 페이지 조회 성공`() {
            // Arrange
            repeat(5) { index ->
                val poll = createTestPoll(title = "페이지네이션 테스트 $index", creatorAccountId = testAccount.id)
                createTestPollOptions(poll.id)
            }

            // Act - 첫 페이지 조회
            val firstPageResponse = pollQueryController.getMyPolls(
                page = 1,
                size = 2,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert - 첫 페이지
            assertThat(firstPageResponse.data).isNotNull
            assertThat(firstPageResponse.data?.content?.size).isLessThanOrEqualTo(2)
            assertThat(firstPageResponse.data?.totalCount).isEqualTo(5)
            assertThat(firstPageResponse.data?.totalPages).isEqualTo(3)

            // Act - 두 번째 페이지 조회
            val secondPageResponse = pollQueryController.getMyPolls(
                page = 2,
                size = 2,
                userDetails = createUserDetails(testAccount.id)
            )

            // Assert - 두 번째 페이지
            assertThat(secondPageResponse.data).isNotNull
            assertThat(secondPageResponse.data?.content?.size).isLessThanOrEqualTo(2)
        }

        @Test
        fun `여론조사가 없는 사용자 조회 시 빈 목록 반환`() {
            // Arrange
            val newAccount = accountRepository.save(
                Account.create(
                    name = "새유저",
                    phoneNumber = "01099998888",
                    password = "password789",
                    gender = Gender.MALE,
                    birthDate = LocalDate.of(2000, 1, 1),
                    ci = "new-ci-${System.currentTimeMillis()}"
                )
            )

            // Act
            val response = pollQueryController.getMyPolls(
                page = 1,
                size = 10,
                userDetails = createUserDetails(newAccount.id)
            )

            // Assert
            assertThat(response.data).isNotNull
            assertThat(response.data?.content).isEmpty()
            assertThat(response.data?.totalCount).isEqualTo(0)
            assertThat(response.data?.totalPages).isEqualTo(0)
        }
    }
}