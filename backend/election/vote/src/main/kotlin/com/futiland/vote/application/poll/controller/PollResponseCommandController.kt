package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.domain.poll.service.PollResponseCommandUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "여론조사 응답", description = "여론조사 응답 제출, 수정, 삭제 API")
@RestController
@RequestMapping("/poll/v1")
class PollResponseCommandController(
    private val pollResponseCommandUseCase: PollResponseCommandUseCase,
) {
    @Operation(
        summary = "여론조사 응답 제출",
        description = """
            여론조사에 응답을 제출합니다.

            **응답 유형별 요청 예시:**
            - SINGLE_CHOICE: {"responseType": "SINGLE_CHOICE", "optionId": 1}
            - MULTIPLE_CHOICE: {"responseType": "MULTIPLE_CHOICE", "optionIds": [1, 2, 3]}
            - SCORE: {"responseType": "SCORE", "scoreValue": 8}

            **주의사항:**
            - responseType은 여론조사의 responseType과 일치해야 합니다
            - 이미 응답한 경우 에러가 발생합니다 (updateResponse 사용)
            - 투표 기간(startAt ~ endAt)에만 응답 가능합니다
            - 인증된 사용자만 응답 가능합니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "응답 제출 성공 (응답 ID 반환)",
                content = [Content(schema = Schema(implementation = Long::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (이미 응답한 경우, 유효하지 않은 옵션, 범위를 벗어난 점수 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @PostMapping("/{pollId}/response")
    fun submitResponse(
        @Parameter(description = "여론조사 ID", required = true)
        @PathVariable pollId: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "여론조사 응답 제출 요청",
            required = true,
            content = [Content(schema = Schema(implementation = PollResponseSubmitRequest::class))]
        )
        @RequestBody request: PollResponseSubmitRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Long> {
        val responseId = pollResponseCommandUseCase.submitResponse(
            pollId = pollId,
            accountId = userDetails.user.accountId,
            request = request
        )
        return HttpApiResponse.of(responseId)
    }

    @Operation(
        summary = "여론조사 응답 수정",
        description = """
            이미 제출한 여론조사 응답을 수정합니다.

            **응답 유형별 요청 예시:**
            - SINGLE_CHOICE: {"responseType": "SINGLE_CHOICE", "optionId": 1}
            - MULTIPLE_CHOICE: {"responseType": "MULTIPLE_CHOICE", "optionIds": [1, 2, 3]}
            - SCORE: {"responseType": "SCORE", "scoreValue": 8}

            **주의사항:**
            - responseType은 여론조사의 responseType과 일치해야 합니다
            - 응답하지 않은 경우 에러가 발생합니다 (submitResponse 사용)
            - 투표 기간(startAt ~ endAt)에만 수정 가능합니다
            - 재투표가 허용된 여론조사만 수정 가능합니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "응답 수정 성공 (응답 ID 반환)",
                content = [Content(schema = Schema(implementation = Long::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (응답하지 않은 경우, 재투표 불가능한 경우, 유효하지 않은 옵션 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @PutMapping("/{pollId}/response")
    fun updateResponse(
        @Parameter(description = "여론조사 ID", required = true)
        @PathVariable pollId: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "여론조사 응답 수정 요청",
            required = true,
            content = [Content(schema = Schema(implementation = PollResponseSubmitRequest::class))]
        )
        @RequestBody request: PollResponseSubmitRequest,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Long> {
        val responseId = pollResponseCommandUseCase.updateResponse(
            pollId = pollId,
            accountId = userDetails.user.accountId,
            request = request
        )
        return HttpApiResponse.of(responseId)
    }

    @Operation(
        summary = "여론조사 응답 삭제",
        description = """
            이미 제출한 여론조사 응답을 삭제합니다.

            **주의사항:**
            - 응답하지 않은 경우 에러가 발생합니다
            - 투표 기간(startAt ~ endAt)에만 삭제 가능합니다
            - 재투표가 허용된 여론조사만 삭제 가능합니다
            - 삭제 후 다시 응답하려면 submitResponse를 사용하세요
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "응답 삭제 성공",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (응답하지 않은 경우, 재투표 불가능한 경우 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @DeleteMapping("/{pollId}/response")
    fun deleteResponse(
        @Parameter(description = "여론조사 ID", required = true)
        @PathVariable pollId: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Unit> {
        pollResponseCommandUseCase.deleteResponse(
            pollId = pollId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(Unit)
    }
}
