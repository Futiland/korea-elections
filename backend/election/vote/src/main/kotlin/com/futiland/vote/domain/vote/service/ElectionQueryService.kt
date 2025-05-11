package com.futiland.vote.domain.vote.service

import com.futiland.vote.domain.vote.dto.election.ElectionDto
import com.futiland.vote.domain.vote.repository.ElectionRepository
import com.futiland.vote.util.SliceContent
import org.springframework.stereotype.Service

@Service
class ElectionQueryService(
    private val electionRepository: ElectionRepository
) : ElectionQueryUseCase {
    override fun findAllElections(size: Int, nextCursor: String?): SliceContent<ElectionDto> {
        val result = electionRepository.findAll(size, nextCursor)
        val content = result.content.map {
            ElectionDto(
                title = it.title,
                status = it.status,
                description = it.description,
                startDateTime = it.startDateTime,
                endDateTime = it.endDateTime,
                createdAt = it.createdAt,
            )
        }
        return SliceContent(content = content, nextCursor = result.nextCursor)
    }
}