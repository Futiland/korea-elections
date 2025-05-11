package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Candidate
import com.futiland.vote.domain.vote.repository.CandidateRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class CandidateRepositoryImpl(
    private val repository: JpaCandidateRepository,
) : CandidateRepository {
    override fun saveAll(candidates: List<Candidate>): List<Candidate> {
        return repository.saveAll(candidates)
    }
}

interface JpaCandidateRepository : JpaRepository<Candidate, Long> {
}