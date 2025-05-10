package com.futiland.vote.domain.account.service

import com.futiland.vote.application.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.dto.response.SignInSuccessResponse
import com.futiland.vote.application.dto.response.SignupSuccessResponse

interface AccountCommandUseCase {
    fun singUp(
        phoneNumber: String,
        password: String,
        identityVerifiedInfoResponse: IdentityVerifiedInfoResponse
    ): SignupSuccessResponse

    fun signIn(
        phoneNumber: String,
        password: String,
    ): SignInSuccessResponse
}