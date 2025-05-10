package com.futiland.vote.application.account.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.dto.response.SignInSuccessResponse
import com.futiland.vote.application.dto.response.SignupSuccessResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account/v1")
class CommandController {
    @PostMapping("/signup")
    fun signUp(): HttpApiResponse<SignupSuccessResponse> {
        TODO("Not yet implemented")
    }

    @PostMapping("/signin")
    fun signIn(): HttpApiResponse<SignInSuccessResponse> {
        TODO("Not yet implemented")
    }


}