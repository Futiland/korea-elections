package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.response.PollDetailResponse
import com.futiland.vote.application.poll.dto.response.PollListResponse
import com.futiland.vote.application.poll.service.PollQueryFacadeUseCase
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "여론조사", description = "여론조사 조회 API")
@RestController
@RequestMapping("/poll/v1")
class PollQueryController(
    private val pollQueryFacadeUseCase: PollQueryFacadeUseCase,
) {
    @Operation(
        summary = "공개 여론조사 목록 조회",
        description = """
            공개 여론조사 목록을 커서 기반 페이징으로 조회합니다.

            **조회 대상:**
            - IN_PROGRESS (진행중) 상태의 여론조사
            - EXPIRED (기간만료) 상태의 여론조사
            - DRAFT, CANCELLED 상태는 조회되지 않습니다

            **커서 기반 페이징:**
            - 첫 페이지 조회 시 nextCursor를 생략하면 최신 여론조사부터 반환됩니다
            - 응답의 nextCursor 값을 다음 요청의 nextCursor 파라미터로 전달하면 다음 페이지를 조회할 수 있습니다
            - hasNext가 false이면 마지막 페이지입니다

            **정렬:**
            - 생성 일시 기준 최신순으로 정렬됩니다

            **투표 여부 (isVoted):**
            - 로그인한 경우: 각 여론조사에 투표했는지 여부 (true/false)
            - 비로그인: 항상 false
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 목록 조회 성공",
                content = [Content(
                    schema = Schema(
                        type = "object",
                        example = """{"content": [{"id": 1, "title": "여론조사 제목", "description": "설명", "responseType": "SINGLE_CHOICE", "status": "IN_PROGRESS", "isRevotable": true, "startAt": "2024-01-01T00:00:00", "endAt": "2024-12-31T23:59:59", "createdAt": "2024-01-01T00:00:00", "responseCount": 100, "options": [{"id": 1, "optionText": "옵션1", "optionOrder": 1}], "isVoted": true, "creatorInfo": {"accountId": 1, "name": "홍길동"}}], "nextCursor": "eyJpZCI6MTIzfQ=="}"""
                    )
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (잘못된 size 값 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @GetMapping("/public")
    fun getPublicPollList(
        @Parameter(description = "한 페이지에 조회할 항목 수", example = "10")
        @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "다음 페이지를 위한 커서 (첫 페이지는 null)", example = "eyJpZCI6MTIzfQ==")
        @RequestParam nextCursor: String? = null,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
    ): HttpApiResponse<SliceContent<PollListResponse>> {
        val accountId = userDetails?.user?.accountId
        val response = pollQueryFacadeUseCase.getPublicPollList(accountId, size, nextCursor)
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "여론조사 상세 조회",
        description = """
            특정 여론조사의 상세 정보를 조회합니다.

            **포함 정보:**
            - 여론조사 기본 정보 (제목, 설명, 질문 유형 등)
            - 선택지 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE인 경우)
            - 상태 정보 (DRAFT, IN_PROGRESS, EXPIRED, CANCELLED 등)
            - 기간 정보 (시작/종료 일시)
            - 생성자 정보
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "여론조사 상세 조회 성공",
                content = [Content(schema = Schema(implementation = PollDetailResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 여론조사",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @GetMapping("/{pollId}")
    fun getPollDetail(
        @Parameter(description = "조회할 여론조사 ID", example = "1")
        @PathVariable pollId: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
    ): HttpApiResponse<PollDetailResponse> {
        val accountId = userDetails?.user?.accountId
        val response = pollQueryFacadeUseCase.getPollDetail(pollId, accountId)
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "내가 만든 여론조사 목록 조회",
        description = """
            로그인한 사용자가 생성한 여론조사 목록을 조회합니다.

            **페이지네이션:**
            - page: 페이지 번호 (1부터 시작)
            - size: 페이지 크기 (기본 10)
            - totalCount: 전체 항목 개수
            - totalPages: 전체 페이지 수

            **포함 정보:**
            - 모든 상태의 여론조사 (DRAFT, IN_PROGRESS, EXPIRED, CANCELLED 등)
            - 생성 일시 기준 최신순으로 정렬됩니다
            - isVoted: 각 여론조사에 투표했는지 여부 (true/false)

            **인증:**
            - JWT 토큰 필수
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "내 여론조사 목록 조회 성공",
                content = [Content(
                    schema = Schema(
                        type = "object",
                        example = """{"content": [{"id": 1, "title": "여론조사 제목", "description": "설명", "responseType": "SINGLE_CHOICE", "status": "IN_PROGRESS", "isRevotable": true, "startAt": "2024-01-01T00:00:00", "endAt": "2024-12-31T23:59:59", "createdAt": "2024-01-01T00:00:00", "responseCount": 100, "options": [{"id": 1, "optionText": "옵션1", "optionOrder": 1}], "isVoted": true, "creatorInfo": {"accountId": 1, "name": "홍길동"}}], "totalCount": 25, "totalPages": 3}"""
                    )
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (잘못된 size, page 값 등)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패 (로그인 필요)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @GetMapping("/my")
    fun getMyPolls(
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "한 페이지에 조회할 항목 수", example = "10")
        @RequestParam(defaultValue = "10") size: Int,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<PageContent<PollListResponse>> {
        val response = pollQueryFacadeUseCase.getMyPolls(userDetails.user.accountId, page, size)
        return HttpApiResponse.of(response)
    }

}
