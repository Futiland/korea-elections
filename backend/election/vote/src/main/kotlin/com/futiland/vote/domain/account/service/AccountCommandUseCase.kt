package com.futiland.vote.domain.account.service

import com.futiland.vote.application.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.dto.response.SignInSuccessResponse
import com.futiland.vote.application.dto.response.SignupSuccessResponse

interface AccountCommandUseCase {
    fun signIn(
        phoneNumber: String,
        password: String,
    ): SignInSuccessResponse

    fun singUp(
        name: String,
        phoneNumber: String,
        password: String,
        identityVerifiedInfoResponse: IdentityVerifiedInfoResponse
    ): SignupSuccessResponse
}