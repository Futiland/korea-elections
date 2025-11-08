package com.futiland.vote.application.poll.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import com.futiland.vote.application.poll.dto.response.PollResultResponse
import com.futiland.vote.domain.poll.service.PollResponseCommandUseCase
import com.futiland.vote.domain.poll.service.PollResultQueryUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/poll/v1")
class PollResponseController(
    private val pollResponseCommandUseCase: PollResponseCommandUseCase,
    private val pollResultQueryUseCase: PollResultQueryUseCase,
) {
    @PostMapping("/{pollId}/response")
    fun submitResponse(
        @PathVariable pollId: Long,
        @RequestBody request: PollResponseSubmitRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Long> {
        val responseId = pollResponseCommandUseCase.submitResponse(
            pollId = pollId,
            accountId = userDetails.user.accountId,
            request = request
        )
        return HttpApiResponse.of(responseId)
    }

    @PutMapping("/{pollId}/response")
    fun updateResponse(
        @PathVariable pollId: Long,
        @RequestBody request: PollResponseSubmitRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Long> {
        val responseId = pollResponseCommandUseCase.updateResponse(
            pollId = pollId,
            accountId = userDetails.user.accountId,
            request = request
        )
        return HttpApiResponse.of(responseId)
    }

    @DeleteMapping("/{pollId}/response")
    fun deleteResponse(
        @PathVariable pollId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<Unit> {
        pollResponseCommandUseCase.deleteResponse(
            pollId = pollId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(Unit)
    }

    @GetMapping("/{pollId}/result")
    fun getPollResult(
        @PathVariable pollId: Long,
    ): HttpApiResponse<PollResultResponse> {
        val response = pollResultQueryUseCase.getPollResult(pollId)
        return HttpApiResponse.of(response)
    }
}
