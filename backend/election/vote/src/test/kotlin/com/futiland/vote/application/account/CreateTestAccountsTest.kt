package com.futiland.vote.application.account

import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.*

/**
 * 로컬 개발 DB에 테스트 계정 20개를 생성하는 테스트
 *
 * 사용법:
 * 1. IntelliJ에서 이 테스트를 우클릭 -> Run
 * 2. 또는 터미널에서: ./gradlew test --tests CreateTestAccountsTest
 *
 * 주의사항:
 * - @Transactional 없이 실행되므로 실제 DB에 데이터가 저장됩니다
 * - @ActiveProfiles를 변경하여 원하는 환경에서 실행할 수 있습니다
 * - 중복 체크를 하므로 여러 번 실행해도 안전합니다
 */
@SpringBootTest
@ActiveProfiles("local") // 로컬 DB 사용
class CreateTestAccountsTest {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    private val logger = LoggerFactory.getLogger(CreateTestAccountsTest::class.java)

    @Test
    fun `로컬 개발 DB에 테스트 계정 20개 생성하기`() {
        logger.info("=".repeat(60))
        logger.info("테스트 계정 생성을 시작합니다...")
        logger.info("=".repeat(60))

        var createdCount = 0
        var skippedCount = 0

        for (i in 0..19) {
            val phoneNumber = String.format("010-0000-%04d", i)
            val name = "테스트$i"
            val ci = UUID.randomUUID().toString()
            val password = "1234"
            val gender = if (i % 2 == 0) Gender.MALE else Gender.FEMALE
            // 다양한 생년월일 생성 (1990~1999년생)
            val birthDate = LocalDate.of(
                1990 + (i % 10),
                (i % 12) + 1,
                minOf((i % 28) + 1, 28)
            )

            // 중복 체크
            val existingAccount = accountRepository.findByCi(ci)
            if (existingAccount != null) {
                logger.info("[$i] 이미 존재하는 계정: $phoneNumber")
                skippedCount++
                continue
            }

            // 전화번호로도 체크 (혹시 같은 전화번호가 있을 수 있으므로)
            try {
                val account = Account.create(
                    name = name,
                    phoneNumber = phoneNumber,
                    password = password,
                    gender = gender,
                    birthDate = birthDate,
                    ci = ci
                )

                accountRepository.save(account)
                logger.info("[$i] 생성 완료: $phoneNumber (이름: $name, 성별: ${gender.description}, 생년월일: $birthDate)")
                createdCount++
            } catch (e: Exception) {
                logger.warn("[$i] 생성 실패: $phoneNumber - ${e.message}")
                skippedCount++
            }
        }

        logger.info("=".repeat(60))
        logger.info("테스트 계정 생성이 완료되었습니다!")
        logger.info("- 생성된 계정: ${createdCount}개")
        logger.info("- 건너뛴 계정: ${skippedCount}개")
        logger.info("- 전화번호: 010-0000-0000 ~ 010-0000-0019")
        logger.info("- 비밀번호: 1234")
        logger.info("=".repeat(60))
    }
}
