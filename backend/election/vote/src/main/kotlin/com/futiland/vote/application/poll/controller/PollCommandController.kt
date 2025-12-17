package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.request.PublicPollCreateRequest
import com.futiland.vote.application.poll.dto.request.PublicPollDraftCreateRequest
import com.futiland.vote.application.poll.dto.request.PollUpdateRequest
import com.futiland.vote.application.poll.dto.request.SystemPollCreateRequest
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
                description = "잘못된 요청 (필수 필드 누락, 유효하지 않은 날짜 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
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
                description = "잘못된 요청 (필수 필드 누락 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
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

    @Operation(
        summary = "시스템 여론조사 생성",
        description = """
            시스템 여론조사를 생성합니다. 관리자만 생성할 수 있으며, 생성 즉시 IN_PROGRESS 상태가 됩니다.

            **질문 유형:**
            - SINGLE_CHOICE: 단일 선택 (options 필수)
            - MULTIPLE_CHOICE: 다중 선택 (options 필수, minSelections/maxSelections 설정 가능)
            - SCORE: 점수제 (options 불필요, minScore/maxScore 설정)

            **주의사항:**
            - endAt은 필수입니다
            - 점수제가 아닌 경우 최소 2개 이상의 옵션이 필요합니다
            - 시스템 여론조사는 공개 목록에 노출되지 않으며 별도 API로 조회합니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "시스템 여론조사 생성 성공",
                content = [Content(schema = Schema(implementation = PollDetailResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락, 유효하지 않은 날짜 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음 (관리자만 생성 가능)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @PostMapping("/system")
    fun createSystemPoll(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "시스템 여론조사 생성 요청",
            required = true,
            content = [Content(schema = Schema(implementation = SystemPollCreateRequest::class))]
        )
        @RequestBody request: SystemPollCreateRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<PollDetailResponse> {
        val response = pollCommandUseCase.createSystemPoll(
            request = request,
            creatorAccountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "여론조사 수정",
        description = """
            여론조사 정보를 수정합니다.

            **수정 가능 항목:**
            - title: 여론조사 제목
            - description: 여론조사 설명
            - startAt: 시작 일시
            - endAt: 종료 일시

            **주의사항:**
            - 모든 필드는 optional이며, 전달된 필드만 수정됩니다
            - 진행 중(IN_PROGRESS) 상태에서도 수정 가능합니다
            - endAt이 startAt보다 이전이면 에러가 발생합니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 수정 성공",
                content = [Content(schema = Schema(implementation = PollDetailResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (유효하지 않은 날짜 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 여론조사",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @PutMapping("/{pollId}")
    fun updatePoll(
        @Parameter(description = "수정할 여론조사 ID", example = "1")
        @PathVariable pollId: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "여론조사 수정 요청",
            required = true,
            content = [Content(schema = Schema(implementation = PollUpdateRequest::class))]
        )
        @RequestBody request: PollUpdateRequest,
    ): HttpApiResponse<PollDetailResponse> {
        val response = pollCommandUseCase.updatePoll(
            pollId = pollId,
            request = request
        )
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "여론조사 삭제",
        description = """
            여론조사를 삭제합니다.

            **주의사항:**
            - 본인이 생성한 여론조사만 삭제 가능합니다
            - DRAFT 상태의 여론조사만 삭제 가능합니다
            - 진행 중이거나 종료된 여론조사는 취소(cancel) API를 사용하세요
            - 삭제 시 관련 응답 데이터도 함께 삭제됩니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 삭제 성공",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (삭제 불가능한 상태 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음 (본인이 생성한 여론조사가 아님)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 여론조사",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @DeleteMapping("/{pollId}")
    fun deletePoll(
        @Parameter(description = "삭제할 여론조사 ID", example = "1")
        @PathVariable pollId: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Unit> {
        pollCommandUseCase.deletePoll(
            pollId = pollId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(Unit)
    }

    @Operation(
        summary = "여론조사 취소",
        description = """
            진행 중인 여론조사를 취소합니다.

            **주의사항:**
            - 본인이 생성한 여론조사만 취소 가능합니다
            - IN_PROGRESS 상태의 여론조사만 취소 가능합니다
            - 취소된 여론조사는 CANCELLED 상태가 됩니다
            - 취소된 여론조사는 공개 목록에서 보이지 않습니다
            - 이미 제출된 응답은 유지됩니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 취소 성공",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (취소 불가능한 상태 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음 (본인이 생성한 여론조사가 아님)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 여론조사",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @PostMapping("/{pollId}/cancel")
    fun cancelPoll(
        @Parameter(description = "취소할 여론조사 ID", example = "1")
        @PathVariable pollId: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Unit> {
        pollCommandUseCase.cancelPoll(
            pollId = pollId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(Unit)
    }
}
