package com.futiland.vote.application.account.controller

import com.futiland.vote.application.account.dto.response.AccountStatsResponse
import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.application.account.dto.response.StopperResponse
import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.domain.account.service.AccountQueryUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Account", description = "계정 관련 API")
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

    @GetMapping("/stopper")
    fun getSignupStopper(): HttpApiResponse<StopperResponse> {
        val response = accountQueryUseCase.getSignupStopper()
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "투표 통계 조회",
        description = """
            로그인한 사용자의 투표 관련 통계를 조회합니다.

            **반환 정보:**
            - 내가 만든 투표 수
            - 내가 참여한 PUBLIC 투표 수
            - 내가 참여한 SYSTEM 투표 수
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "투표 통계 조회 성공",
                content = [Content(schema = Schema(implementation = AccountStatsResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 필요",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @GetMapping("/info/stats")
    fun getAccountStats(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<AccountStatsResponse> {
        val response = accountQueryUseCase.getAccountStats(userDetails.user.accountId)
        return HttpApiResponse.of(response)
    }
}