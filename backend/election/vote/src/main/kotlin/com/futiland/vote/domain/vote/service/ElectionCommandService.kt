package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.ElectionCreateResponse
import com.futiland.vote.domain.vote.entity.Election
import com.futiland.vote.domain.vote.repository.ElectionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ElectionCommandService(
    private val electionRepository: ElectionRepository,
) : ElectionCommandUseCase {
    override fun createActive(
        title: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        description: String,
    ): ElectionCreateResponse {
        val election = Election.createActive(
            title = title,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            description = description,
        )
        val savedElection = electionRepository.save(election)
        return ElectionCreateResponse(
            id= savedElection.id,
            createdAt = savedElection.createdAt,
        )
    }

    override fun delete(id: Long): Long {
        val election = electionRepository.getById(id = id)
        election.delete()
        val savedElection = electionRepository.save(election)
        return savedElection.id
    }

}