package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.request.SignUpRequest
import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.dto.response.VerificationResponse
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import org.springframework.stereotype.Service

@Service
class AccountCommandFacadeService(
    private val accountCommandUseCase: AccountCommandUseCase,
    private val accountQueryUseCase: AccountQueryUseCase,
    private val identityVerificationPort: IdentityVerificationPort,
) : AccountCommandFacadeUseCase {
    override fun signUp(request: SignUpRequest): SignupSuccessResponse {
        val verificationResponse = try {
            identityVerificationPort.verify(identityVerificationId = request.verificationId)
        } catch (e: Exception) {
            throw ApplicationException(
                code = CodeEnum.FRS_004,
                "본인인증에 실패했습니다. 잘못된 경로로 가입을 시도하셨습니다."
            )
        }

        checkSignUpRequirements(verificationResponse)

        return accountCommandUseCase.singUp(
            phoneNumber = verificationResponse.verifiedCustomer.phoneNumber,
            password = request.password,
            identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
                name = verificationResponse.verifiedCustomer.name,
                gender = Gender.valueOf(verificationResponse.verifiedCustomer.gender),
                birthDate = verificationResponse.verifiedCustomer.birthDate,
                ci = verificationResponse.verifiedCustomer.ci
            )
        )
    }

    /**
     * 가입자 조건 검사 : 중복가입 방지, 투표권이 있는 자만 가입 가능
     */
    private fun checkSignUpRequirements(verificationResponse: VerificationResponse) {
        if (isNotKoreanCitizen(verificationResponse)) throw ApplicationException(
            code = CodeEnum.FRS_002,
            message = "외국인은 가입할 수 없습니다."
        )

        if (isNotAdaptedAge(verificationResponse)) throw ApplicationException(
            code = CodeEnum.FRS_003,
            message = "만 18세 이상만 가입할 수 있습니다."
        )

        if (isAlreadySignedUp(verificationResponse)) throw ApplicationException(
            code = CodeEnum.FRS_003,
            "이미 가입된 유저입니다. 비밀번호를 찾고자한다면 joonhee.alert@gmail.com으로 연락주세요."
        )
    }

    private fun isAlreadySignedUp(verificationResponse: VerificationResponse): Boolean {
        return accountQueryUseCase.isAlreadySignedUp(ci = verificationResponse.verifiedCustomer.ci)
    }

    private fun isNotAdaptedAge(verificationResponse: VerificationResponse): Boolean {
        val now = java.time.LocalDate.now()
        val age = java.time.Period.between(verificationResponse.verifiedCustomer.birthDate, now).years
        if (age < 18) {
            return false
        }
        return true
    }

    private fun isNotKoreanCitizen(verificationResponse: VerificationResponse): Boolean {
        return verificationResponse.verifiedCustomer.isForeigner
    }
}