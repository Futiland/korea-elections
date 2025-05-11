package com.futiland.vote.application.vote.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.vote.dto.request.CandidateDeleteRequest
import com.futiland.vote.application.vote.dto.request.CandidateAddRequest
import com.futiland.vote.application.vote.dto.request.ElectionCreateRequest
import com.futiland.vote.application.vote.dto.request.ElectionDeleteRequest
import com.futiland.vote.application.vote.dto.response.CandidateCreateResponse
import com.futiland.vote.application.vote.dto.response.CandidateDeleteResponse
import com.futiland.vote.application.vote.dto.response.ElectionCreateResponse
import com.futiland.vote.application.vote.dto.response.ElectionDeleteResponse
import com.futiland.vote.domain.vote.service.ElectionCommandUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/election/v1")
class ElectionCommandController(
    private val electionCommandUseCase: ElectionCommandUseCase,
    private val candidateCommandUseCase: ElectionCommandUseCase
) {
    @PostMapping("")
    fun create(
        @RequestBody request: ElectionCreateRequest
    ): HttpApiResponse<ElectionCreateResponse> {
        val response = electionCommandUseCase.createActive(
            title = request.title,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            description = request.description,
        )
        return HttpApiResponse.of(response)
    }

    @DeleteMapping("")
    fun delete(
        @RequestBody request: ElectionDeleteRequest
    ): HttpApiResponse<ElectionDeleteResponse> {
        val response = electionCommandUseCase.delete(
            id = request.electionId
        )
        return HttpApiResponse.of(response)
    }

    @PostMapping("/candidate")
    fun addCandidate(
        @RequestBody request: CandidateAddRequest
    ): HttpApiResponse<CandidateCreateResponse> {
        val response = candidateCommandUseCase.addCandidate(
            electionId = request.electionId,
            candidateDto = request.candidates
        )
        return HttpApiResponse.of(response)
    }

    @DeleteMapping("/candidate")
    fun deleteCandidate(
        @RequestBody request: CandidateDeleteRequest
    ): HttpApiResponse<CandidateDeleteResponse> {
        val response = candidateCommandUseCase.deleteCandidate(
            electionId = request.electionId,
            candidateIds = request.candidateIds
        )
        return HttpApiResponse.of(response)
    }
}