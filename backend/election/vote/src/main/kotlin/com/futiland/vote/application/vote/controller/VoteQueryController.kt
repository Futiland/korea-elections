package com.futiland.vote.application.vote.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.vote.dto.response.VoteResultResponse
import com.futiland.vote.domain.vote.entity.Candidate
import com.futiland.vote.domain.vote.service.CandidateQueryUseCase
import com.futiland.vote.domain.vote.service.VoteQueryUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/election/v1")
class VoteQueryController(
    private val candidateQueryUseCase: CandidateQueryUseCase,
    private val voteQueryUseCase: VoteQueryUseCase
) {
    @GetMapping("/{electionId}/vote")
    fun getCandidates(
        @PathVariable electionId: Long,
    ): HttpApiResponse<List<Candidate>> {
        val response = candidateQueryUseCase.findAllCandidateByElectionId(id = electionId)
        return HttpApiResponse.of(response)
    }

    @GetMapping("/{electionId}/vote/result")
    fun getResult(
        @PathVariable electionId: Long,
    ): HttpApiResponse<VoteResultResponse> {
        val response = voteQueryUseCase.getResult(electionId = electionId)
        return HttpApiResponse.of(response)
    }
}