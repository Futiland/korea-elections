package com.futiland.vote.application.account.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.account.dto.request.SignInRequest
import com.futiland.vote.application.account.dto.request.SignUpRequest
import com.futiland.vote.application.account.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.account.dto.response.SignInSuccessResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse
import com.futiland.vote.domain.account.service.AccountCommandFacadeUseCase
import com.futiland.vote.domain.account.service.AccountCommandUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account/v1")
class CommandController(
    private val accountCommandUseCase: AccountCommandUseCase,
    private val accountCommandFacadeUseCase: AccountCommandFacadeUseCase
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody request: SignUpRequest
    ): HttpApiResponse<SignupSuccessResponse> {
        val response = accountCommandFacadeUseCase.signUp(request)
        return HttpApiResponse.of(response)
    }

    @PostMapping("/signin")
    fun signIn(
        @RequestBody request: SignInRequest
    ): HttpApiResponse<SignInSuccessResponse> {
        val response = accountCommandUseCase.signIn(
            phoneNumber = request.phoneNumber,
            password = request.password,
        )
        return HttpApiResponse.of(response)
    }


}