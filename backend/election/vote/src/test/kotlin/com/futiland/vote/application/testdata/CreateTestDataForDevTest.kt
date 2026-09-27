package com.futiland.vote.application.testdata

import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollOption
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.entity.ResponseType
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import com.futiland.vote.domain.poll.service.PollResponseCommandService
import com.futiland.vote.domain.account.service.AccountCommandService
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Dev 환경 DB에 테스트 데이터를 생성하는 테스트
 *
 * 사용법:
 * 1. vote/.env 파일에 환경변수가 설정되어 있어야 합니다
 * 2. IntelliJ에서 이 테스트를 우클릭 -> Run
 * 3. 또는 터미널에서:
 *    cd vote
 *    ./gradlew test --tests "*.CreateTestDataForDevTest"
 *
 * 주의사항:
 * - @Transactional 없이 실행되므로 실제 DB에 데이터가 저장됩니다
 * - dev 환경 DB에 저장됩니다 (RDS)
 * - 중복 체크를 하므로 여러 번 실행해도 안전합니다
 */
@SpringBootTest
class CreateTestDataForDevTest {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var pollRepository: PollRepository

    @Autowired
    private lateinit var pollOptionRepository: PollOptionRepository

    @Autowired
    private lateinit var pollResponseRepository: PollResponseRepository

    @Autowired
    private lateinit var pollResponseCommandService: PollResponseCommandService

    @Autowired
    private lateinit var accountCommandService: AccountCommandService

    private val logger = LoggerFactory.getLogger(CreateTestDataForDevTest::class.java)

    companion object {
        const val TEST_USER_COUNT = 30
        const val TEST_CI_PREFIX = "test-dev-ci-"

        // ========================================
        // 기존 Poll에 투표할 때 사용할 Poll ID
        // DB에서 조회 후 여기에 설정하세요
        // ========================================
        const val TARGET_POLL_ID = 24L
    }

    // ========================================
    // 헬퍼 메서드: 테스트 계정 조회 또는 생성
    // ========================================

    /**
     * 테스트 계정을 조회하거나 없으면 생성합니다.
     * @return 테스트 계정 목록
     */
    private fun getOrCreateTestAccounts(): List<Account> {
        logger.info("테스트 계정 조회/생성 시작...")

        val accounts = mutableListOf<Account>()
        var createdCount = 0
        var existingCount = 0

        for (i in 0 until TEST_USER_COUNT) {
            val ci = "$TEST_CI_PREFIX$i"
            val phoneNumber = String.format("010-9999-%04d", i)
            val name = "Dev테스트$i"

            val existingAccount = accountRepository.findByCi(ci)
            if (existingAccount != null) {
                accounts.add(existingAccount)
                existingCount++
            } else {
                try {
                    val account = Account.create(
                        name = name,
                        phoneNumber = phoneNumber,
                        password = "1234",
                        gender = if (i % 2 == 0) Gender.MALE else Gender.FEMALE,
                        birthDate = LocalDate.of(1990 + (i % 10), (i % 12) + 1, 1),
                        ci = ci
                    )
                    val savedAccount = accountRepository.save(account)
                    accounts.add(savedAccount)
                    createdCount++
                    logger.info("[$i] 새 계정 생성: $phoneNumber (ID: ${savedAccount.id})")
                } catch (e: Exception) {
                    logger.error("[$i] 계정 생성 실패: ${e.message}")
                }
            }
        }

        logger.info("테스트 계정 준비 완료: 기존 ${existingCount}개, 신규 ${createdCount}개")
        return accounts
    }

    // ========================================
    // 투표 헬퍼 메서드 (서비스 사용)
    // ========================================

    /**
     * 단일선택 Poll에 투표합니다.
     * 서비스를 통해 투표하므로 재투표 로직도 자동 처리됩니다.
     */
    private fun voteOnSingleChoicePoll(
        pollId: Long,
        accounts: List<Account>,
        optionIndex: Int? = null  // null이면 순환하며 투표
    ): Int {
        val options = pollOptionRepository.findAllByPollId(pollId)
        if (options.isEmpty()) {
            logger.error("Poll ID=$pollId 에 선택지가 없습니다.")
            return 0
        }

        var voteCount = 0
        accounts.forEachIndexed { idx, account ->
            val selectedOption = if (optionIndex != null) {
                options[optionIndex % options.size]
            } else {
                options[idx % options.size]
            }

            try {
                val request = PollResponseSubmitRequest.SingleChoice(optionId = selectedOption.id)
                pollResponseCommandService.submitResponse(pollId, account.id, null, request)
                logger.info("투표 완료: 계정ID=${account.id} -> '${selectedOption.optionText}'")
                voteCount++
            } catch (e: Exception) {
                logger.warn("투표 실패: 계정ID=${account.id} - ${e.message}")
            }
        }

        return voteCount
    }

    /**
     * 다중선택 Poll에 투표합니다.
     * 서비스를 통해 투표하므로 재투표 로직도 자동 처리됩니다.
     */
    private fun voteOnMultipleChoicePoll(
        pollId: Long,
        accounts: List<Account>,
        selectCount: Int = 2  // 각 유저가 선택할 개수
    ): Int {
        val options = pollOptionRepository.findAllByPollId(pollId)
        if (options.isEmpty()) {
            logger.error("Poll ID=$pollId 에 선택지가 없습니다.")
            return 0
        }

        var voteCount = 0
        accounts.forEachIndexed { idx, account ->
            val count = minOf(selectCount + (idx % 2), options.size)
            val selectedOptions = options.shuffled().take(count)

            try {
                val request = PollResponseSubmitRequest.MultipleChoice(optionIds = selectedOptions.map { it.id })
                pollResponseCommandService.submitResponse(pollId, account.id, null, request)
                logger.info("투표 완료: 계정ID=${account.id} -> ${selectedOptions.map { it.optionText }}")
                voteCount++
            } catch (e: Exception) {
                logger.warn("투표 실패: 계정ID=${account.id} - ${e.message}")
            }
        }

        return voteCount
    }

    /**
     * 점수제 Poll에 투표합니다.
     * 서비스를 통해 투표하므로 재투표 로직도 자동 처리됩니다.
     */
    private fun voteOnScorePoll(
        pollId: Long,
        accounts: List<Account>,
        minScore: Int = 0,
        maxScore: Int = 10
    ): Int {
        var voteCount = 0
        accounts.forEach { account ->
            val score = (minScore..maxScore).random()

            try {
                val request = PollResponseSubmitRequest.Score(scoreValue = score)
                pollResponseCommandService.submitResponse(pollId, account.id, null, request)
                logger.info("투표 완료: 계정ID=${account.id} -> ${score}점")
                voteCount++
            } catch (e: Exception) {
                logger.warn("투표 실패: 계정ID=${account.id} - ${e.message}")
            }
        }

        return voteCount
    }

    // ========================================
    // 테스트 메서드
    // ========================================

    @Test
    fun `테스트 계정 조회 또는 생성`() {
        logger.info("=" .repeat(60))
        val accounts = getOrCreateTestAccounts()
        logger.info("=" .repeat(60))
        logger.info("테스트 계정 목록:")
        accounts.forEach { account ->
            logger.info("- ID: ${account.id}, 이름: ${account.name}, 전화번호: ${account.phoneNumber}")
        }
        logger.info("- 비밀번호: 1234")
        logger.info("=" .repeat(60))
    }

    @Test
    fun `기존 Poll에 투표하기`() {
        logger.info("=" .repeat(60))
        logger.info("기존 Poll에 투표를 시작합니다... (Poll ID: $TARGET_POLL_ID)")
        logger.info("=" .repeat(60))

        // 1. Poll 조회
        val poll = pollRepository.findById(TARGET_POLL_ID)
        if (poll == null) {
            logger.error("Poll ID=$TARGET_POLL_ID 를 찾을 수 없습니다.")
            return
        }

        logger.info("Poll 정보: ID=${poll.id}, 제목='${poll.title}', 타입=${poll.responseType}")

        // 2. 테스트 계정 준비
        val accounts = getOrCreateTestAccounts()

        // 3. Poll 타입에 따라 투표
        val voteCount = when (poll.responseType) {
            ResponseType.SINGLE_CHOICE -> voteOnSingleChoicePoll(poll.id, accounts)
            ResponseType.MULTIPLE_CHOICE -> voteOnMultipleChoicePoll(poll.id, accounts)
            ResponseType.SCORE -> voteOnScorePoll(poll.id, accounts)
        }

        logger.info("=" .repeat(60))
        logger.info("투표 완료! 총 ${voteCount}건 투표됨")
        logger.info("=" .repeat(60))
    }

    @Test
    fun `새 단일선택 Poll 생성 및 투표`() {
        logger.info("=" .repeat(60))
        logger.info("단일선택 Poll 생성 및 투표 시작...")
        logger.info("=" .repeat(60))

        val accounts = getOrCreateTestAccounts()
        val creatorAccount = accounts.first()

        // Poll 생성
        val poll = Poll.createActive(
            title = "[테스트] 좋아하는 계절은?",
            description = "Dev 환경 테스트용 여론조사입니다.",
            responseType = ResponseType.SINGLE_CHOICE,
            pollType = PollType.PUBLIC,
            isRevotable = false,
            creatorAccountId = creatorAccount.id,
            endAt = LocalDateTime.now().plusDays(30)
        )
        val savedPoll = pollRepository.save(poll)
        logger.info("Poll 생성 완료: ID=${savedPoll.id}")

        // 선택지 생성
        val optionTexts = listOf("봄", "여름", "가을", "겨울")
        val options = optionTexts.mapIndexed { idx, text ->
            PollOption.create(savedPoll.id, text, idx + 1)
        }
        val savedOptions = pollOptionRepository.saveAll(options)
        logger.info("선택지 생성 완료: ${savedOptions.map { it.optionText }}")

        // 투표
        val voteCount = voteOnSingleChoicePoll(savedPoll.id, accounts)

        logger.info("=" .repeat(60))
        logger.info("완료! Poll ID: ${savedPoll.id}, 투표: ${voteCount}건")
        logger.info("=" .repeat(60))
    }

    @Test
    fun `새 다중선택 Poll 생성 및 투표`() {
        logger.info("=" .repeat(60))
        logger.info("다중선택 Poll 생성 및 투표 시작...")
        logger.info("=" .repeat(60))

        val accounts = getOrCreateTestAccounts()
        val creatorAccount = accounts.first()

        // Poll 생성
        val poll = Poll.createActive(
            title = "[테스트] 관심 있는 정책 분야 (복수선택)",
            description = "Dev 환경 테스트용 다중선택 여론조사입니다.",
            responseType = ResponseType.MULTIPLE_CHOICE,
            pollType = PollType.PUBLIC,
            isRevotable = true,
            creatorAccountId = creatorAccount.id,
            endAt = LocalDateTime.now().plusDays(30)
        )
        val savedPoll = pollRepository.save(poll)
        logger.info("Poll 생성 완료: ID=${savedPoll.id}")

        // 선택지 생성
        val optionTexts = listOf("경제", "복지", "외교", "환경", "교육")
        val options = optionTexts.mapIndexed { idx, text ->
            PollOption.create(savedPoll.id, text, idx + 1)
        }
        val savedOptions = pollOptionRepository.saveAll(options)
        logger.info("선택지 생성 완료: ${savedOptions.map { it.optionText }}")

        // 투표
        val responseCount = voteOnMultipleChoicePoll(savedPoll.id, accounts, selectCount = 2)

        logger.info("=" .repeat(60))
        logger.info("완료! Poll ID: ${savedPoll.id}, 응답 레코드: ${responseCount}건")
        logger.info("=" .repeat(60))
    }

    @Test
    fun `새 점수제 Poll 생성 및 투표`() {
        logger.info("=" .repeat(60))
        logger.info("점수제 Poll 생성 및 투표 시작...")
        logger.info("=" .repeat(60))

        val accounts = getOrCreateTestAccounts()
        val creatorAccount = accounts.first()

        // Poll 생성
        val poll = Poll.createActive(
            title = "[테스트] 현 정부 지지도 평가",
            description = "Dev 환경 테스트용 점수제 여론조사입니다. 0~10점으로 평가해주세요.",
            responseType = ResponseType.SCORE,
            pollType = PollType.PUBLIC,
            isRevotable = true,
            creatorAccountId = creatorAccount.id,
            endAt = LocalDateTime.now().plusDays(30)
        )
        val savedPoll = pollRepository.save(poll)
        logger.info("Poll 생성 완료: ID=${savedPoll.id}")

        // 투표 (0~10점 랜덤)
        val voteCount = voteOnScorePoll(savedPoll.id, accounts)

        logger.info("=" .repeat(60))
        logger.info("완료! Poll ID: ${savedPoll.id}, 투표: ${voteCount}건")
        logger.info("=" .repeat(60))
    }

    @Test
    fun `진행중인 Poll 목록 조회`() {
        logger.info("=" .repeat(60))
        logger.info("진행중인 Poll 목록 조회...")
        logger.info("=" .repeat(60))

        val polls = pollRepository.findAllPublicDisplayable(size = 20, nextCursor = null)

        if (polls.content.isEmpty()) {
            logger.info("진행중인 Poll이 없습니다.")
        } else {
            polls.content.forEach { poll ->
                val responseCount = pollResponseRepository.countByPollId(poll.id)
                logger.info("- ID: ${poll.id}, 제목: '${poll.title}', 타입: ${poll.responseType}, 투표수: $responseCount")
            }
        }

        logger.info("=" .repeat(60))
    }

    // ========================================
    // 테스트 계정 삭제 (탈퇴 처리)
    // ========================================

    @Test
    fun `테스트 계정 탈퇴 처리`() {
        logger.info("=" .repeat(60))
        logger.info("테스트 계정 탈퇴 처리 시작...")
        logger.info("CI 패턴: ${TEST_CI_PREFIX}*")
        logger.info("=" .repeat(60))

        var deletedCount = 0
        var failedCount = 0
        var notFoundCount = 0

        for (i in 0 until TEST_USER_COUNT) {
            val ci = "$TEST_CI_PREFIX$i"
            val account = accountRepository.findByCi(ci)

            if (account == null) {
                notFoundCount++
                continue
            }

            if (account.deletedAt != null) {
                logger.info("[$i] 이미 탈퇴된 계정: ID=${account.id}")
                continue
            }

            try {
                accountCommandService.deleteAccount(account.id)
                logger.info("[$i] 탈퇴 완료: ID=${account.id}, CI=$ci")
                deletedCount++
            } catch (e: Exception) {
                logger.error("[$i] 탈퇴 실패: ID=${account.id}, CI=$ci - ${e.message}")
                failedCount++
            }
        }

        logger.info("=" .repeat(60))
        logger.info("탈퇴 처리 완료!")
        logger.info("- 탈퇴 성공: ${deletedCount}건")
        logger.info("- 탈퇴 실패: ${failedCount}건")
        logger.info("- 계정 없음: ${notFoundCount}건")
        logger.info("=" .repeat(60))
    }
}
