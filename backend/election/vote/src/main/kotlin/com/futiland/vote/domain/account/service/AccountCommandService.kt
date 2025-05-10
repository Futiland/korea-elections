package com.futiland.vote.domain.account.service

import com.futiland.vote.application.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.dto.response.SignInSuccessResponse
import com.futiland.vote.application.dto.response.SignupSuccessResponse
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.repository.AccountRepository

class AccountCommandService(
    private val accountRepository: AccountRepository,
) : AccountCommandUseCase {
    override fun singUp(
        phoneNumber: String,
        password: String,
        identityVerifiedInfoResponse: IdentityVerifiedInfoResponse
    ): SignupSuccessResponse {
        val account = Account.create(
            phoneNumber = phoneNumber,
            password = password,
            gender = identityVerifiedInfoResponse.gender,
            birthDate = identityVerifiedInfoResponse.birthDate,
            ci = identityVerifiedInfoResponse.ci
        )
        accountRepository.save(account)
        return SignupSuccessResponse(
            id = account.id,
            createdAt = account.createdAt,
        )
    }

    override fun signIn(phoneNumber: String, password: String): SignInSuccessResponse {
        val account = accountRepository.getByPhoneNumberAndPassword(
            phoneNumber = phoneNumber, password = password
        )
        return SignInSuccessResponse(
            token = "token",
        )
    }
}