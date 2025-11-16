package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.application.poll.dto.response.PollResultResponse
import com.futiland.vote.domain.poll.service.PollResponseCommandUseCase
import com.futiland.vote.domain.poll.service.PollResultQueryUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "여론조사 응답", description = "여론조사 응답 제출, 수정, 삭제 및 결과 조회 API")
@RestController
@RequestMapping("/poll/v1")
class PollResponseController(
    private val pollResponseCommandUseCase: PollResponseCommandUseCase,
    private val pollResultQueryUseCase: PollResultQueryUseCase,
) {
    @Operation(
        summary = "여론조사 응답 제출",
        description = """
            여론조사에 응답을 제출합니다.

            **응답 유형별 요청:**
            - SINGLE_CHOICE: optionIds에 1개의 옵션 ID 전달
            - MULTIPLE_CHOICE: optionIds에 여러 개의 옵션 ID 전달 (minSelections ~ maxSelections 범위 내)
            - SCORE: scoreValue에 점수 전달 (minScore ~ maxScore 범위 내)

            **주의사항:**
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
                description = "잘못된 요청 (이미 응답한 경우, 유효하지 않은 옵션, 범위를 벗어난 점수 등)"
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패"
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음"
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

            **응답 유형별 요청:**
            - SINGLE_CHOICE: optionIds에 1개의 옵션 ID 전달
            - MULTIPLE_CHOICE: optionIds에 여러 개의 옵션 ID 전달 (minSelections ~ maxSelections 범위 내)
            - SCORE: scoreValue에 점수 전달 (minScore ~ maxScore 범위 내)

            **주의사항:**
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
                description = "잘못된 요청 (응답하지 않은 경우, 재투표 불가능한 경우, 유효하지 않은 옵션 등)"
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패"
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음"
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
                description = "응답 삭제 성공"
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (응답하지 않은 경우, 재투표 불가능한 경우 등)"
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패"
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음"
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

    @Operation(
        summary = "여론조사 결과 조회",
        description = """
            여론조사의 집계 결과를 조회합니다.

            **응답 내용:**
            - 전체 응답 수
            - 질문 유형별 집계 결과
            - SINGLE_CHOICE/MULTIPLE_CHOICE: 각 옵션별 득표 수와 비율
            - SCORE: 평균 점수, 최소/최대 점수 등

            **주의사항:**
            - 인증 없이 조회 가능합니다
            - 진행 중이거나 종료된 여론조사의 결과를 확인할 수 있습니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결과 조회 성공",
                content = [Content(schema = Schema(implementation = PollResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음"
            )
        ]
    )
    @GetMapping("/{pollId}/result")
    fun getPollResult(
        @Parameter(description = "여론조사 ID", required = true)
        @PathVariable pollId: Long,
    ): HttpApiResponse<PollResultResponse> {
        val response = pollResultQueryUseCase.getPollResult(pollId)
        return HttpApiResponse.of(response)
    }
}
