package com.futiland.vote.application.account.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.domain.account.service.AccountQueryUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account/v1")
class QueryController(
    private val accountQueryUseCase: AccountQueryUseCase,
) {
    @GetMapping("/info/profile")
    fun getAccountInfo(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<ProfileResponse> {
        val response = accountQueryUseCase.getProfileById(userDetails.user.accountId)
        return HttpApiResponse.of(response)
    }
}