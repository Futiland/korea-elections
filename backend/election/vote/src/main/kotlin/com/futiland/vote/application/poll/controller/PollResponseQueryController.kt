package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.response.ParticipatedPollResponse
import com.futiland.vote.application.poll.dto.response.PollResultResponse
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.service.PollQueryUseCase
import com.futiland.vote.domain.poll.service.PollResultQueryUseCase
import com.futiland.vote.util.PageContent
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "여론조사 응답", description = "여론조사 결과 및 참여 내역 조회 API")
@RestController
@RequestMapping("/poll/v1")
class PollResponseQueryController(
    private val pollResultQueryUseCase: PollResultQueryUseCase,
    private val pollQueryUseCase: PollQueryUseCase,
) {
    @Operation(
        summary = "여론조사 결과 조회",
        description = """
            여론조사의 집계 결과를 조회합니다.

            **응답 내용:**
            - 전체 응답 수
            - 질문 유형별 집계 결과
              - SINGLE_CHOICE/MULTIPLE_CHOICE: 각 옵션별 득표 수와 비율
              - SCORE: 평균 점수, 최소/최대 점수, 점수별 분포
            - 내 응답 정보 (myResponse)
              - SINGLE_CHOICE: selectedOptionId
              - MULTIPLE_CHOICE: selectedOptionIds
              - SCORE: scoreValue
              - 공통: createdAt, updatedAt

            **비로그인 사용자:**
            - anonymous_session 쿠키가 있으면 해당 쿠키로 myResponse를 조회합니다
            - 비로그인 투표 후 같은 브라우저에서 결과 조회 시 내 응답 정보 확인 가능

            **주의사항:**
            - 투표에 참여한 사용자만 결과를 조회할 수 있습니다
            - 진행 중이거나 종료된 여론조사의 결과를 확인할 수 있습니다
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결과 조회 성공",
                content = [Content(
                    schema = Schema(
                        type = "object",
                        example = """{
                            "pollId": 1,
                            "responseType": "SINGLE_CHOICE",
                            "totalResponseCount": 1234,
                            "optionResults": [
                                {"optionId": 1, "optionText": "매우 찬성", "voteCount": 500, "percentage": 40.5},
                                {"optionId": 2, "optionText": "찬성", "voteCount": 300, "percentage": 24.3}
                            ],
                            "scoreResult": null,
                            "myResponse": {
                                "selectedOptionId": 1,
                                "createdAt": "2024-01-15T10:30:00",
                                "updatedAt": null
                            }
                        }"""
                    )
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패 (로그인 필요)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음 (투표에 참여하지 않은 사용자)",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "여론조사를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = HttpApiResponse::class))]
            )
        ]
    )
    @GetMapping("/{pollId}/result")
    fun getPollResult(
        @Parameter(description = "여론조사 ID", required = true)
        @PathVariable pollId: Long,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails?,
        @Parameter(
            description = "비로그인 사용자 식별용 쿠키 (서버가 Set-Cookie로 발급, 브라우저가 자동 전송. 내 응답 조회에 사용)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = false
        )
        @CookieValue("anonymous_session", required = false) pollSession: String?,
    ): HttpApiResponse<PollResultResponse> {
        val accountId = userDetails?.user?.accountId
        val anonymousSessionId = if (accountId == null) pollSession else null
        val response = pollResultQueryUseCase.getPollResult(pollId, accountId, anonymousSessionId)
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "내가 참여한 모두의 투표 목록 조회",
        description = """
            로그인한 사용자가 참여한 **공개(PUBLIC)** 여론조사 목록을 조회합니다.

            **페이지네이션:**
            - page: 페이지 번호 (1부터 시작)
            - size: 페이지 크기 (기본 10)
            - totalCount: 전체 항목 개수
            - totalPages: 전체 페이지 수

            **특징:**
            - No Offset 방식으로 빠른 성능 (커버링 인덱스 활용)
            - 참여 일시 기준 최신순 정렬
            - 전체 개수 포함

            **인증:**
            - JWT 토큰 필수
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "내가 참여한 모두의 투표 목록 조회 성공",
                content = [Content(
                    schema = Schema(
                        type = "object",
                        example = """{
                            "content": [
                                {
                                    "id": 1,
                                    "title": "2024년 대통령 지지율 조사",
                                    "description": "현 대통령의 지지율을 조사합니다.",
                                    "responseType": "SINGLE_CHOICE",
                                    "status": "IN_PROGRESS",
                                    "isRevotable": true,
                                    "startAt": "2024-01-01T00:00:00",
                                    "endAt": "2024-12-31T23:59:59",
                                    "participatedAt": "2024-06-15T14:30:00",
                                    "responseId": 100
                                }
                            ],
                            "totalCount": 25,
                            "totalPages": 3
                        }"""
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
    @GetMapping("/public/response/my")
    fun getMyPublicParticipatedPolls(
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "한 페이지에 조회할 항목 수", example = "10")
        @RequestParam(defaultValue = "10") size: Int,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<PageContent<ParticipatedPollResponse>> {
        val response = pollQueryUseCase.getParticipatedPolls(
            accountId = userDetails.user.accountId,
            page = page,
            size = size,
            pollType = PollType.PUBLIC
        )
        return HttpApiResponse.of(response)
    }

    @Operation(
        summary = "내가 참여한 시스템 여론조사 목록 조회",
        description = """
            로그인한 사용자가 참여한 **시스템(SYSTEM)** 여론조사 목록을 조회합니다.

            **페이지네이션:**
            - page: 페이지 번호 (1부터 시작)
            - size: 페이지 크기 (기본 10)
            - totalCount: 전체 항목 개수
            - totalPages: 전체 페이지 수

            **특징:**
            - No Offset 방식으로 빠른 성능 (커버링 인덱스 활용)
            - 참여 일시 기준 최신순 정렬
            - 전체 개수 포함

            **인증:**
            - JWT 토큰 필수
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "내가 참여한 시스템 여론조사 목록 조회 성공",
                content = [Content(
                    schema = Schema(
                        type = "object",
                        example = """{
                            "content": [
                                {
                                    "id": 1,
                                    "title": "2024년 대통령 지지율 조사",
                                    "description": "현 대통령의 지지율을 조사합니다.",
                                    "responseType": "SINGLE_CHOICE",
                                    "status": "IN_PROGRESS",
                                    "isRevotable": true,
                                    "startAt": "2024-01-01T00:00:00",
                                    "endAt": "2024-12-31T23:59:59",
                                    "participatedAt": "2024-06-15T14:30:00",
                                    "responseId": 100
                                }
                            ],
                            "totalCount": 25,
                            "totalPages": 3
                        }"""
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
    @GetMapping("/system/response/my")
    fun getMySystemParticipatedPolls(
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "한 페이지에 조회할 항목 수", example = "10")
        @RequestParam(defaultValue = "10") size: Int,
        @Parameter(hidden = true)
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<PageContent<ParticipatedPollResponse>> {
        val response = pollQueryUseCase.getParticipatedPolls(
            accountId = userDetails.user.accountId,
            page = page,
            size = size,
            pollType = PollType.SYSTEM
        )
        return HttpApiResponse.of(response)
    }
}
