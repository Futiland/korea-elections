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
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

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

            **비로그인 투표 (anonymous_session 쿠키):**
            - 여론조사의 allowAnonymousVote=true인 경우, 로그인 없이도 투표 가능
            - 비로그인 사용자는 서버가 발급하는 anonymous_session 쿠키로 식별됩니다
            - 최초 투표 시 쿠키가 없으면 서버가 자동 발급 (Set-Cookie 응답 헤더)
            - 이후 요청 시 브라우저가 쿠키를 자동 전송하여 동일 사용자 식별
            - 쿠키 속성: HttpOnly, Secure, Max-Age=1년
            - 민심투표(SYSTEM)는 비로그인 투표 불가 (항상 로그인 필수)

            **주의사항:**
            - responseType은 여론조사의 responseType과 일치해야 합니다
            - 이미 응답한 경우 에러가 발생합니다 (updateResponse 사용)
            - 투표 기간(startAt ~ endAt)에만 응답 가능합니다
            - isRevotable 설정은 로그인/비로그인 동일하게 적용됩니다
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
                description = "인증 실패 (비로그인 투표가 허용되지 않는 경우)",
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
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
        @Parameter(
            description = "비로그인 사용자 식별용 쿠키 (최초 투표 시 서버가 Set-Cookie로 발급, 이후 브라우저가 자동 전송)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = false
        )
        @CookieValue("anonymous_session", required = false) pollSession: String?,
        @Parameter(hidden = true)
        response: HttpServletResponse,
    ): HttpApiResponse<Long> {
        val accountId = userDetails?.user?.accountId

        // 비로그인 사용자: 쿠키 없으면 새로 발급
        val anonymousSessionId = if (accountId == null) {
            val sessionId = pollSession ?: UUID.randomUUID().toString()
            if (pollSession == null) {
                addPollSessionCookie(response, sessionId)
            }
            sessionId
        } else null

        val responseId = pollResponseCommandUseCase.submitResponse(
            pollId = pollId,
            accountId = accountId,
            anonymousSessionId = anonymousSessionId,
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

            **비로그인 사용자:**
            - anonymous_session 쿠키로 기존 응답을 식별하여 수정합니다
            - 쿠키가 없는 경우 서버가 새로 발급합니다

            **주의사항:**
            - responseType은 여론조사의 responseType과 일치해야 합니다
            - 응답하지 않은 경우 에러가 발생합니다 (submitResponse 사용)
            - 투표 기간(startAt ~ endAt)에만 수정 가능합니다
            - 재투표가 허용된(isRevotable=true) 여론조사만 수정 가능합니다
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
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
        @Parameter(
            description = "비로그인 사용자 식별용 쿠키 (최초 투표 시 서버가 Set-Cookie로 발급, 이후 브라우저가 자동 전송)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = false
        )
        @CookieValue("anonymous_session", required = false) pollSession: String?,
        @Parameter(hidden = true)
        response: HttpServletResponse,
    ): HttpApiResponse<Long> {
        val accountId = userDetails?.user?.accountId

        val anonymousSessionId = if (accountId == null) {
            val sessionId = pollSession ?: UUID.randomUUID().toString()
            if (pollSession == null) {
                addPollSessionCookie(response, sessionId)
            }
            sessionId
        } else null

        val responseId = pollResponseCommandUseCase.updateResponse(
            pollId = pollId,
            accountId = accountId,
            anonymousSessionId = anonymousSessionId,
            request = request
        )
        return HttpApiResponse.of(responseId)
    }

    @Operation(
        summary = "여론조사 응답 삭제",
        description = """
            이미 제출한 여론조사 응답을 삭제합니다.

            **비로그인 사용자:**
            - anonymous_session 쿠키로 기존 응답을 식별하여 삭제합니다

            **주의사항:**
            - 응답하지 않은 경우 에러가 발생합니다
            - 투표 기간(startAt ~ endAt)에만 삭제 가능합니다
            - 재투표가 허용된(isRevotable=true) 여론조사만 삭제 가능합니다
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
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
        @Parameter(
            description = "비로그인 사용자 식별용 쿠키 (최초 투표 시 서버가 Set-Cookie로 발급, 이후 브라우저가 자동 전송)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = false
        )
        @CookieValue("anonymous_session", required = false) pollSession: String?,
    ): HttpApiResponse<Unit> {
        val accountId = userDetails?.user?.accountId
        val anonymousSessionId = if (accountId == null) pollSession else null

        pollResponseCommandUseCase.deleteResponse(
            pollId = pollId,
            accountId = accountId,
            anonymousSessionId = anonymousSessionId
        )
        return HttpApiResponse.of(Unit)
    }

    private fun addPollSessionCookie(response: HttpServletResponse, sessionId: String) {
        val cookie = Cookie("anonymous_session", sessionId).apply {
            path = "/"
            isHttpOnly = true
            maxAge = 365 * 24 * 60 * 60 // 1년
            secure = true
        }
        response.addCookie(cookie)
    }
}
