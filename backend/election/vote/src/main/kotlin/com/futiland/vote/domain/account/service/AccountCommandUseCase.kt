package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.account.dto.response.ChangePasswordResponse
import com.futiland.vote.application.account.dto.response.SignInSuccessResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse

interface AccountCommandUseCase {
    fun signIn(
        phoneNumber: String,
        password: String,
    ): SignInSuccessResponse

    fun singUp(
        phoneNumber: String,
        password: String,
        identityVerifiedInfoResponse: IdentityVerifiedInfoResponse
    ): SignupSuccessResponse

    fun changePassword(
        verificationId: String,
        password: String,
    ): ChangePasswordResponse

    fun deleteAccount(accountId: Long)

    /**
     * 재가입 대기 기간이 지난 삭제된 계정의 CI를 익명화
     * @param ci 본인인증 CI
     */
    fun anonymizeDeletedAccountIfEligible(ci: String)
}