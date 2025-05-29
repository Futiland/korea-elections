package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.request.SignUpRequest
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse

interface AccountCommandFacadeUseCase {
    fun signUp(request: SignUpRequest): SignupSuccessResponse
}