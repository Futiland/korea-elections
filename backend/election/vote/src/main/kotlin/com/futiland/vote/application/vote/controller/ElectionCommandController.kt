package com.futiland.vote.application.vote.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.vote.dto.request.ElectionCreateRequest
import com.futiland.vote.application.vote.dto.response.ElectionCreateResponse
import com.futiland.vote.domain.vote.service.ElectionCommandUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/election/v1")
class ElectionCommandController(
    private val electionCommandUseCase: ElectionCommandUseCase,
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

}