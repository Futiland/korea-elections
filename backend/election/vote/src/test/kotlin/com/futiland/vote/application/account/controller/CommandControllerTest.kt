package com.futiland.vote.application.account.controller

import com.futiland.vote.application.dto.request.SignUpRequest
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
class CommandControllerTest {
    @Autowired
    lateinit var accountCommandController: CommandController

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `회원가입 성공`() {
        // Arrange
        val request = SignUpRequest(
            name = "test",
            phoneNumber = "01012345678",
            password = "password",
            gender = Gender.MALE,
            birthDate = LocalDate.of(1970, 1, 1),
            ci = "ci",
        )
        // Action
        val response = accountCommandController.signUp(
            request = request
        )
        // Assert
        val accountId = response.data?.id ?: throw IllegalArgumentException("Account ID is null")
        val account = accountRepository.getById(accountId)
        assertThat(accountId).isNotNull()
    }

}