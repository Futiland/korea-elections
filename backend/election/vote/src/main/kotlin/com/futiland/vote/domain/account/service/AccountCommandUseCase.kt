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
}