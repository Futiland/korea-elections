package com.futiland.vote.application.vote.controller

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.config.security.CustomUserDetails
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.vote.dto.response.CandidateQueryResponse
import com.futiland.vote.application.vote.dto.response.MyVoteResponse
import com.futiland.vote.domain.vote.dto.result.*
import com.futiland.vote.domain.vote.service.CandidateQueryUseCase
import com.futiland.vote.domain.vote.service.VoteQueryUseCase
import com.futiland.vote.domain.vote.service.ResultQueryUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/election/v1")
class VoteQueryController(
    private val candidateQueryUseCase: CandidateQueryUseCase,
    private val voteQueryUseCase: VoteQueryUseCase,
    private val resultQueryUseCase: ResultQueryUseCase
) {
    @GetMapping("/{electionId}/vote")
    fun getCandidates(
        @PathVariable electionId: Long,
    ): HttpApiResponse<List<CandidateQueryResponse>> {
        val response = candidateQueryUseCase.findAllCandidateByElectionId(id = electionId)
        return HttpApiResponse.of(response)
    }

    @GetMapping("/{electionId}/vote/result")
    fun getResult(
        @PathVariable electionId: Long,
        @RequestParam(required = false, defaultValue = "ALL") resultType: ResultType,
    ): HttpApiResponse<VoteResult> {
        val response = when (resultType) {
            ResultType.ALL -> voteQueryUseCase.getResult(electionId)
            ResultType.AGE -> resultQueryUseCase.getResultByAge(electionId)
            ResultType.GENDER -> resultQueryUseCase.getResultByGender(electionId)
        }
        return HttpApiResponse.of(response)
    }

    @GetMapping("/{electionId}/vote/results")
    suspend fun getResultByType(
        @PathVariable electionId: Long,
        @RequestParam resultType: ResultType,
    ): HttpApiResponse<AgeGroupResultResponse> {
        val response = resultQueryUseCase.getResultByAge(electionId)
        return HttpApiResponse.of(response)
    }

    @GetMapping("/{electionId}/vote/results2")
    suspend fun getResultByType2(
        @PathVariable electionId: Long,
        @RequestParam resultType: ResultType,
    ): HttpApiResponse<GenderGroupResultResponse> {
        val response = resultQueryUseCase.getResultByGender(electionId)
        return HttpApiResponse.of(response)
    }

    @GetMapping("/{electionId}/vote/mine")
    fun getMyResult(
        @PathVariable electionId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): HttpApiResponse<MyVoteResponse?> {
        val response = voteQueryUseCase.findMyVote(electionId = electionId, accountId = userDetails.user.accountId)
        return HttpApiResponse.of(response)
    }
}