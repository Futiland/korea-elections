package com.futiland.vote.application.account.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.dto.request.SignUpRequest
import com.futiland.vote.application.dto.response.IdentityVerifiedInfoResponse
import com.futiland.vote.application.dto.response.SignInSuccessResponse
import com.futiland.vote.application.dto.response.SignupSuccessResponse
import com.futiland.vote.domain.account.service.AccountCommandUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account/v1")
class CommandController(
    private val accountCommandUseCase: AccountCommandUseCase,
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody request: SignUpRequest
    ): HttpApiResponse<SignupSuccessResponse> {
        val response = accountCommandUseCase.singUp(
            name = request.name,
            phoneNumber = request.phoneNumber,
            password = request.password,
            identityVerifiedInfoResponse = IdentityVerifiedInfoResponse(
                gender = request.gender,
                birthDate = request.birthDate,
                ci = request.ci,
            ),
        )
        return HttpApiResponse.of(response)
    }

    @PostMapping("/signin")
    fun signIn(): HttpApiResponse<SignInSuccessResponse> {
        TODO("Not yet implemented")
    }


}