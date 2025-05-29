package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.account.dto.response.SignInSuccessResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse
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
        phoneNumber: String,
        password: String,
        identityVerifiedInfoResponse: IdentityVerifiedInfoResponse
    ): SignupSuccessResponse {
        val account = Account.create(
            name = identityVerifiedInfoResponse.name,
            phoneNumber = identityVerifiedInfoResponse.phoneNumber,
            password = password,
            gender = identityVerifiedInfoResponse.gender,
            birthDate = identityVerifiedInfoResponse.birthDate,
            ci = identityVerifiedInfoResponse.ci
        )
        val savedAccount = accountRepository.save(account)
        val payload = getAccountJwtPayload(account)
        val token = jwtTokenProvider.generateToken(payload, accessTokenTtl)
        return SignupSuccessResponse(
            id = savedAccount.id,
            createdAt = savedAccount.createdAt,
            token =token
        )
    }

    override fun signIn(phoneNumber: String, password: String): SignInSuccessResponse {
        val account = accountRepository.getByPhoneNumberAndPassword(
            phoneNumber = phoneNumber, password = password
        )

        val payload = getAccountJwtPayload(account)

        val token = jwtTokenProvider.generateToken(payload = payload, ttl = accessTokenTtl)
        return SignInSuccessResponse(
            token = token,
        )
    }

    private fun getAccountJwtPayload(account: Account): Map<String, Any> {
        return AccountJwtPayload(
            accountId = account.id,
        ).toMap()
    }
}