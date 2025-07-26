package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.account.dto.response.ChangePasswordResponse
import com.futiland.vote.application.account.dto.response.SignInSuccessResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.common.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AccountCommandService(
    private val accountRepository: AccountRepository,
    private val verificationPort: IdentityVerificationPort,
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
            phoneNumber = phoneNumber,
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
            token = token
        )
    }

    override fun changePassword(verificationId: String, password: String): ChangePasswordResponse {
        val verificationResponse = verificationPort.verify(verificationId)
        val account =
            accountRepository.findByCi(ci = verificationResponse.verifiedCustomer.ci) ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "해당 가입된 사용자가 없습니다. 가입을 먼저 진행해주세요."
            )
        val token = jwtTokenProvider.generateToken(
            payload = getAccountJwtPayload(account),
            ttl = accessTokenTtl
        )
        return ChangePasswordResponse(
            token = token
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