package com.futiland.vote.application.vote.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.vote.dto.request.VoteCommitRequest
import com.futiland.vote.application.vote.dto.response.VoteCommitResponse
import com.futiland.vote.domain.vote.service.VoteCommandUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/election/v1")
class VoteCommandController(
    private val voteCommandUseCase: VoteCommandUseCase,
) {
    @PostMapping("/{electionId}/vote")
    fun commit(
        @PathVariable electionId: Long,
        @RequestBody request: VoteCommitRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): HttpApiResponse<VoteCommitResponse> {
        val response = voteCommandUseCase.commit(
            electionId = electionId,
            candidateId = request.candidateId,
            accountId = userDetails.user.accountId
        )
        return HttpApiResponse.of(response)
    }
}