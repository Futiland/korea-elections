package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.repository.FakeAccountRepository
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import kotlin.test.Test

class AccountQueryServiceTest {

    private lateinit var accountQueryUseCase: AccountQueryUseCase
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        accountQueryUseCase = AccountQueryService(
            accountRepository = accountRepository
        )
    }

    @Test
    fun `단일 Profile 조회`() {
        // Arrange
        val account = Account.create(
            phoneNumber = "01012345678",
            password = "password",
            name = "test",
            gender = Gender.MALE,
            birthDate = LocalDate.now(),
            ci = "ci",
        )
        accountRepository.save(account)

        // Action
        val profileResponse = accountQueryUseCase.getProfileById(id = account.id)

        // Assert
        assertThat(profileResponse.id).isEqualTo(account.id)
        assertThat(profileResponse.phoneNumber).isEqualTo(account.phoneNumber)
        assertThat(profileResponse.name).isEqualTo(account.name)
    }
}