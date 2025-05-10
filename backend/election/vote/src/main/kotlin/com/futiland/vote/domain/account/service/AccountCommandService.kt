package com.futiland.vote.domain.account.service

import com.futiland.vote.application.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.dto.response.SignInSuccessResponse
import com.futiland.vote.application.dto.response.SignupSuccessResponse
import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.common.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AccountCommandService(
    private val accountRepository: AccountRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${access_token.ttl}")
    private val accessTokenTtl: Int
) : AccountCommandUseCase {
    override fun singUp(
        name: String,
        phoneNumber: String,
        password: String,
        identityVerifiedInfoResponse: IdentityVerifiedInfoResponse
    ): SignupSuccessResponse {
        val account = Account.create(
            name = name,
            phoneNumber = phoneNumber,
            password = password,
            gender = identityVerifiedInfoResponse.gender,
            birthDate = identityVerifiedInfoResponse.birthDate,
            ci = identityVerifiedInfoResponse.ci
        )
        val savedAccount = accountRepository.save(account)
        return SignupSuccessResponse(
            id = savedAccount.id,
            createdAt = savedAccount.createdAt,
        )
    }

    override fun signIn(phoneNumber: String, password: String): SignInSuccessResponse {
        val account = accountRepository.getByPhoneNumberAndPassword(
            phoneNumber = phoneNumber, password = password
        )

        val payload = AccountJwtPayload(
            accountId = account.id,
        ).toMap()

        val token = jwtTokenProvider.generateToken(payload = payload, ttl = accessTokenTtl)
        return SignInSuccessResponse(
            token = token,
        )
    }
}