package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Candidate
import com.futiland.vote.domain.vote.entity.CandidateStatus
import com.futiland.vote.domain.vote.repository.CandidateRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
class CandidateRepositoryImpl(
    private val repository: JpaCandidateRepository,
) : CandidateRepository {
    override fun saveAll(candidates: List<Candidate>): List<Candidate> {
        return repository.saveAll(candidates)
    }

    override fun findByIds(ids: List<Long>): List<Candidate> {
        return repository.findByIdIn(ids)
    }

    override fun findByElectionId(electionId: Long): List<Candidate> {
        return repository.findByElectionId(electionId, listOf(CandidateStatus.ACTIVE))
    }
}

interface JpaCandidateRepository : JpaRepository<Candidate, Long> {
    fun findByIdIn(ids: List<Long>): List<Candidate>

    @Query("select c from Candidate c join fetch c.party where c.electionId = :electionId and c.status in (:status)")
    fun findByElectionId(electionId: Long, status: List<CandidateStatus>): List<Candidate>
}