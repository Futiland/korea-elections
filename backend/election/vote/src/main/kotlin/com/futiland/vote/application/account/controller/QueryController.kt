package com.futiland.vote.application.account.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.dto.response.ProfileResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account/v1")
class QueryController {
    @RequestMapping("/info/profile")
    fun getAccountInfo(): HttpApiResponse<ProfileResponse> {
        TODO()
    }
}