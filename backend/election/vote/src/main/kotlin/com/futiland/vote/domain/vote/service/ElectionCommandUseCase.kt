package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.CandidateCreateResponse
import com.futiland.vote.application.vote.dto.response.ElectionCreateResponse
import com.futiland.vote.application.vote.dto.response.ElectionDeleteResponse
import com.futiland.vote.domain.vote.dto.candidate.CandidateDto
import java.time.LocalDateTime

interface ElectionCommandUseCase {
    fun createActive(
        title: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        description: String,
//        candidates: List<String>,
    ): ElectionCreateResponse

    fun delete(id: Long): ElectionDeleteResponse

    fun addCandidate(
        electionId: Long,
        candidateDto: List<CandidateDto>,
    ): CandidateCreateResponse
}