package com.futiland.vote.application.account.controller

import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.config.security.CustomUserDto
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("h2-test")
@Transactional
class QueryControllerTest {

    @Autowired
    lateinit var accountQueryController: QueryController

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `로그인 유저의 프로필 보기`() {
        // Arrange
        val name = "testName"
        val phoneNumber = "010123456"
        val password = "testPassword"
        val gender = Gender.MALE
        val birthDate = LocalDate.of(1990, 1, 1)
        val ci = "ci"
        val account = Account.create(
            name = name,
            phoneNumber = phoneNumber,
            password = password,
            gender = gender,
            birthDate = birthDate,
            ci = ci
        )
        val savedAccount = accountRepository.save(account)
        // Action
        val response = accountQueryController.getAccountInfo(
            userDetails = CustomUserDetails(
                user = CustomUserDto(
                    accountId = savedAccount.id
                )
            )
        )
        // Assert
        val profileAccount = response.data ?: throw IllegalArgumentException("Profile data is null")
        assertThat(profileAccount.id).isEqualTo(savedAccount.id)
    }

}