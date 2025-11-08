package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PublicPollDraftCreateRequest
import com.futiland.vote.application.poll.dto.request.PollUpdateRequest
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.domain.poll.service.PollCommandUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "여론조사", description = "여론조사 생성, 수정, 삭제 API")
@RestController
@RequestMapping("/poll/v1")
class PollCommandController(
    private val pollCommandUseCase: PollCommandUseCase,
) {
    @Operation(
        summary = "공개 여론조사 생성",
        description = """
            공개 여론조사를 생성합니다. 생성 즉시 IN_PROGRESS 상태가 되며 설정된 기간 동안 응답을 받을 수 있습니다.

            **질문 유형:**
            - SINGLE_CHOICE: 단일 선택 (options 필수)
            - MULTIPLE_CHOICE: 다중 선택 (options 필수, minSelections/maxSelections 설정 가능)
            - SCORE: 점수제 (options 불필요, minScore/maxScore 설정)

            **주의사항:**
            - startAt과 endAt은 필수입니다
            - 점수제가 아닌 경우 최소 2개 이상의 옵션이 필요합니다
            - 다중 선택의 경우 minSelections ≤ maxSelections 이어야 합니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 생성 성공",
                content = [Content(schema = Schema(implementation = PollDetailResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락, 유효하지 않은 날짜 등)"
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패"
            )
        ]
    )
    @PostMapping("/public")
    fun createPublicPoll(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "공개 여론조사 생성 요청",
            required = true,
            content = [Content(schema = Schema(implementation = PublicPollCreateRequest::class))]
        )
        @RequestBody request: PublicPollCreateRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<PollDetailResponse> {
        val response = pollCommandUseCase.createPublicPoll(
            request = request,
            creatorAccountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "공개 여론조사 임시저장",
        description = """
            공개 여론조사를 임시저장합니다. DRAFT 상태로 생성되며 startAt과 endAt 없이 저장됩니다.

            **질문 유형:**
            - SINGLE_CHOICE: 단일 선택 (options 필수)
            - MULTIPLE_CHOICE: 다중 선택 (options 필수, minSelections/maxSelections 설정 가능)
            - SCORE: 점수제 (options 불필요, minScore/maxScore 설정)

            **주의사항:**
            - DRAFT 상태에서는 응답을 받을 수 없습니다
            - 나중에 수정하여 시작/종료 시간을 설정하고 IN_PROGRESS 상태로 전환할 수 있습니다
            - 점수제가 아닌 경우 최소 2개 이상의 옵션이 필요합니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 임시저장 성공",
                content = [Content(schema = Schema(implementation = PollDetailResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락 등)"
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패"
            )
        ]
    )
    @PostMapping("/public/draft")
    fun createPublicPollDraft(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "공개 여론조사 임시저장 요청",
            required = true,
            content = [Content(schema = Schema(implementation = PublicPollDraftCreateRequest::class))]
        )
        @RequestBody request: PublicPollDraftCreateRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<PollDetailResponse> {
        val response = pollCommandUseCase.createPublicPollDraft(
            request = request,
            creatorAccountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(response)
    }

    @PutMapping("/{pollId}")
    fun updatePoll(
        @PathVariable pollId: Long,
        @RequestBody request: PollUpdateRequest,
    ): HttpApiResponse<PollDetailResponse> {
        val response = pollCommandUseCase.updatePoll(
            pollId = pollId,
            request = request
        )
        return HttpApiResponse.of(response)
    }

    @DeleteMapping("/{pollId}")
    fun deletePoll(
        @PathVariable pollId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Unit> {
        pollCommandUseCase.deletePoll(
            pollId = pollId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(Unit)
    }

    @PostMapping("/{pollId}/cancel")
    fun cancelPoll(
        @PathVariable pollId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Unit> {
        pollCommandUseCase.cancelPoll(
            pollId = pollId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(Unit)
    }
}
