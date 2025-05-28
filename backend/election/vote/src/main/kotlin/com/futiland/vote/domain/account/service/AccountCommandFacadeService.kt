package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.request.SignUpRequest
import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import org.springframework.stereotype.Service

@Service
class AccountCommandFacadeService(
    private val accountCommandUseCase: AccountCommandUseCase,
    private val identityVerificationPort: IdentityVerificationPort,
) : AccountCommandFacadeUseCase {
    override fun signUp(request: SignUpRequest): SignupSuccessResponse {
        val verificationResponse = try{
            identityVerificationPort.verify(identityVerificationId = request.verificationId)
        }catch (e: Exception) {
            // TODO("예외컨트롤러 만들어서 처리하기")
            throw e
        }
        // TODO CI 이미 있으면 예외처리하기
        return accountCommandUseCase.singUp(
            phoneNumber = request.phoneNumber,
            password = request.password,
            identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
                name = verificationResponse.verifiedCustomer.name,
                gender = Gender.valueOf(verificationResponse.verifiedCustomer.gender),
                birthDate = verificationResponse.verifiedCustomer.birthDate,
                ci = verificationResponse.verifiedCustomer.ci
            )
        )
    }
}