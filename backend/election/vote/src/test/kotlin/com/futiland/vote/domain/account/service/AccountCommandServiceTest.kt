package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.repository.FakeAccountRepository
import com.futiland.vote.application.common.JwtTokenProviderImpl
import com.futiland.vote.application.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
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
    val SECRET_KEY = "SECRET_KEY"

    @BeforeEach
    fun setUp() {
        this.accountRepository = FakeAccountRepository()
        this.jwtTokenProvider = JwtTokenProviderImpl(
            secretKey = SECRET_KEY,
        )
        this.accountCommandUseCase = AccountCommandService(
            accountRepository = accountRepository,
            jwtTokenProvider = jwtTokenProvider,
            accessTokenTtl = 3600
        )
    }

    @Nested
    inner class `회원가입` {
        @Test
        fun `회원가입 성공`() {
            // Arrange
            val account = Account.create(
                name = "test",
                phoneNumber = "01012345678",
                password = "password",
                gender = Gender.MALE,
                birthDate = LocalDate.now(),
                ci = "ci",
            )
            // Action
            val newAccount = accountCommandUseCase.singUp(
                name = account.name,
                phoneNumber = account.phoneNumber,
                password = account.password,
                identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
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
                ci = "ci",
            )
            val newAccount = accountCommandUseCase.singUp(
                name = account.name,
                phoneNumber = account.phoneNumber,
                password = account.password,
                identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
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