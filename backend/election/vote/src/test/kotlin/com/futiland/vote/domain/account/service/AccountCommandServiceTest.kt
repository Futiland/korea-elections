package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.repository.FakeAccountRepository
import com.futiland.vote.application.common.JwtTokenProviderImpl
import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.domain.account.dto.response.VerificationResponse
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.common.JwtTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import java.time.LocalDate
import kotlin.test.Test

class AccountCommandServiceTest {

    lateinit var accountCommandUseCase: AccountCommandUseCase
    lateinit var accountRepository: AccountRepository
    lateinit var jwtTokenProvider: JwtTokenProvider
    lateinit var identityVerificationPort: IdentityVerificationPort
    val staticCi = "ci"
    val SECRET_KEY = "SECRET_KEY"

    @BeforeEach
    fun setUp() {
        this.accountRepository = FakeAccountRepository()
        this.identityVerificationPort = object : IdentityVerificationPort {
            override fun verify(verificationId: String): VerificationResponse {
                // 테스트용 stub/mock 리턴
                return VerificationResponse(
                    status = "SUCCESS",
                    id = "test-id",
                    channel = VerificationResponse.Channel(
                        type = "type",
                        id = "id",
                        key = "key",
                        name = "name",
                        pgProvider = "pgProvider",
                        pgMerchantId = "pgMerchantId"
                    ),
                    verifiedCustomer = VerificationResponse.VerifiedCustomer(
                        id = "ID",
                        name = "test",
                        operator = "OPERATOR",
                        phoneNumber = "01012345678",
                        birthDate = LocalDate.parse("2000-01-01"),
                        gender = "MALE",
                        isForeigner = false,
                        ci = staticCi,
                        di = "DI"
                    ),
                    requestedAt = "2024-01-01T00:00:00",
                    updatedAt = "2024-01-01T00:00:00",
                    statusChangedAt = "2024-01-01T00:00:00",
                    verifiedAt = "2024-01-01T00:00:00",
                    pgTxId = "pgTxId",
                    pgRawResponse = "{}",
                    version = "1"
                )
            }
        }
        this.jwtTokenProvider = JwtTokenProviderImpl(
            secretKey = SECRET_KEY,
        )
        this.accountCommandUseCase = AccountCommandService(
            accountRepository = accountRepository,
            verificationPort = identityVerificationPort,
            jwtTokenProvider = jwtTokenProvider,
            accessTokenTtl = 3600
        )
    }

    @Nested
    inner class `회원가입` {
        @Test
        fun `회원가입 성공`() {
            // Arrange
            val name = "test"
            val account = Account.create(
                name = name,
                phoneNumber = "01012345678",
                password = "password",
                gender = Gender.MALE,
                birthDate = LocalDate.now(),
                ci = staticCi,
            )
            // Action
            val newAccount = accountCommandUseCase.singUp(
                phoneNumber = account.phoneNumber,
                password = account.password,
                identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
                    name = name,
                    gender = account.gender,
                    birthDate = account.birthDate,
                    ci = account.ci,
                )
            )

            // Assert
            val savedAccount = accountRepository.getByPhoneNumberAndPassword(
                phoneNumber = account.phoneNumber,
                password = account.password
            )
            assertThat(newAccount.id).isEqualTo(savedAccount.id)
        }
    }

    @Nested
    inner class `로그인` {
        @Test
        fun `로그인 성공`() {
            // Arrange
            val account = Account.create(
                name = "test",
                phoneNumber = "01012345678",
                password = "password",
                gender = Gender.MALE,
                birthDate = LocalDate.now(),
                ci = staticCi,
            )
            val newAccount = accountCommandUseCase.singUp(
                phoneNumber = account.phoneNumber,
                password = account.password,
                identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
                    name = account.name,
                    gender = account.gender,
                    birthDate = account.birthDate,
                    ci = account.ci,
                )
            )

            // Action
            val signInResponse = accountCommandUseCase.signIn(
                phoneNumber = account.phoneNumber,
                password = account.password
            )

            // Assert
            assertThat(signInResponse.token).isNotNull()
        }

    }
}