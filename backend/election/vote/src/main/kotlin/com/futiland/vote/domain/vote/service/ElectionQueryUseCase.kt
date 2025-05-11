package com.futiland.vote.domain.vote.service

import com.futiland.vote.domain.vote.dto.election.ElectionDto
import com.futiland.vote.util.SliceContent

interface ElectionQueryUseCase {
    fun findAllElections(
        size: Int, nextCursor: String?
    ): SliceContent<ElectionDto>
}