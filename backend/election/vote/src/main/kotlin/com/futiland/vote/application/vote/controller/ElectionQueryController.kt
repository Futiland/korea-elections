package com.futiland.vote.application.vote.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.domain.vote.dto.election.ElectionDto
import com.futiland.vote.domain.vote.entity.Candidate
import com.futiland.vote.domain.vote.service.CandidateQueryUseCase
import com.futiland.vote.domain.vote.service.ElectionQueryUseCase
import com.futiland.vote.util.SliceContent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/election/v1")
class ElectionQueryController(
    private val electionQueryUseCase: ElectionQueryUseCase,
    private val candidateQueryUseCase: CandidateQueryUseCase,
) {
    @GetMapping("")
    fun getElections(
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam nextCursor: String? = null,
    ): HttpApiResponse<SliceContent<ElectionDto>> {
        val response = electionQueryUseCase.findAllElections(size, nextCursor)
        return HttpApiResponse.of(response)
    }

    @GetMapping("/{electionId}/candidate")
    fun getCandidates(
        @PathVariable electionId: Long,
    ): HttpApiResponse<List<Candidate>> {
        val response = candidateQueryUseCase.findAllCandidateByElectionId(id=electionId)
        return HttpApiResponse.of(response)
    }
}