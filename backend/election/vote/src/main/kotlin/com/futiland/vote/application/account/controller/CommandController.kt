package com.futiland.vote.application.account.controller

import com.futiland.vote.application.account.dto.request.ChangePasswordRequest
import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.account.dto.request.SignInRequest
import com.futiland.vote.application.account.dto.request.SignUpRequest
import com.futiland.vote.application.account.dto.request.VerificationRequest
import com.futiland.vote.application.account.dto.response.ChangePasswordResponse
import com.futiland.vote.application.account.dto.response.SignInSuccessResponse
import com.futiland.vote.application.account.dto.response.SignupSuccessResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.domain.account.dto.response.VerifiedResponse
import com.futiland.vote.domain.account.service.AccountCommandFacadeUseCase
import com.futiland.vote.domain.account.service.AccountCommandUseCase
import com.futiland.vote.domain.account.service.MobileIdentifyUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account/v1")
class CommandController(
    private val accountCommandUseCase: AccountCommandUseCase,
    private val accountCommandFacadeUseCase: AccountCommandFacadeUseCase,
//    private val identifyUseCase: MobileIdentifyUseCase,
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

    /**
     * TODO : 현재 그냥 비밀번호를 변경하지만 추후 암호화된 requestBody를 받아서 변경을 해야할 것으로 보임.(현재는 빠른 작업을 위해서 그냥 처리)
     */
    @PostMapping("/change-password")
    fun changePassword(
        @RequestBody request: ChangePasswordRequest
    ): HttpApiResponse<ChangePasswordResponse> {
        val response = accountCommandUseCase.changePassword(
            verificationId = request.verificationId,
            password = request.password,
        )
        return HttpApiResponse.of(response)
    }

    @DeleteMapping("/me")
    fun deleteAccount(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): HttpApiResponse<Unit> {
        accountCommandUseCase.deleteAccount(userDetails.user.accountId)
        return HttpApiResponse.of(Unit)
    }

}