package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.ElectionCreateResponse
import java.time.LocalDateTime

interface ElectionCommandUseCase {
    fun createActive(
        title: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        description: String,
//        candidates: List<String>,
    ): ElectionCreateResponse

    fun delete(id: Long): Long
}