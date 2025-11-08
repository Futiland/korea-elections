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
        // 1. 본인인증 검증
        val verificationResponse =
            identityVerificationPort.verify(identityVerificationId = request.verificationId)

        val ci = verificationResponse.verifiedCustomer.ci

        // 2. 가입 요건 검증 (나이, 국적)
        checkSignUpRequirements(verificationResponse)

        // 3. 가입 가능 여부 검증 (중복 체크 + 재가입 대기 기간)
        // 단일 DB 조회로 성능 최적화
        accountQueryUseCase.validateSignUpEligibility(ci = ci)

        // 4. 재가입 가능한 삭제된 계정의 CI 익명화 (Command - 상태 변경)
        // Note: findByCi() 2번 호출되지만 CI는 UNIQUE INDEX가 있어 성능 영향 미미 (~1ms)
        accountCommandUseCase.anonymizeDeletedAccountIfEligible(ci = ci)

        // 5. 신규 계정 생성
        return accountCommandUseCase.singUp(
            phoneNumber = verificationResponse.verifiedCustomer.phoneNumber,
            password = request.password,
            identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
                name = verificationResponse.verifiedCustomer.name,
                gender = Gender.valueOf(verificationResponse.verifiedCustomer.gender),
                birthDate = verificationResponse.verifiedCustomer.birthDate,
                ci = ci
            )
        )
    }

    /**
     * 가입자 조건 검사: 투표권이 있는 자만 가입 가능 (나이, 국적)
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
    }

    private fun isNotAdaptedAge(verificationResponse: VerificationResponse): Boolean {
        val now = java.time.LocalDate.now()
        val age = java.time.Period.between(verificationResponse.verifiedCustomer.birthDate, now).years
        if (age < 18) {
            return true
        }
        return false
    }

    private fun isNotKoreanCitizen(verificationResponse: VerificationResponse): Boolean {
        return verificationResponse.verifiedCustomer.isForeigner
    }
}