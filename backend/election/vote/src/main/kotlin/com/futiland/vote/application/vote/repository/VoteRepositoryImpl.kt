package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Vote
import com.futiland.vote.domain.vote.repository.VoteRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class VoteRepositoryImpl(
    private val repository: JpaVoteRepository
) : VoteRepository {
    override fun findByElectionIdAndAccountId(electionId: Long, accountId: Long): Vote? {
        return repository.findByElectionIdAndAccountId(electionId, accountId)?.let { return it }
    }

    override fun findByElectionId(electionId: Long, lastVoteId: Long?, limit: Int): List<Vote> {
        return if (lastVoteId == null) {
            repository.findByElectionIdAfterId(electionId = electionId, lastVoteId = null)
        } else {
            repository.findByElectionIdAfterId(electionId, lastVoteId)
        }
    }

    override fun save(vote: Vote): Vote {
        return repository.save(vote)
    }

    override fun findByElectionIdAndCandidateId(electionId: Long, candidateId: Long): Long {
        return repository.countVotesByElectionIdAndCandidateId(electionId, candidateId)
    }

    override fun findLatestTimeByElectionId(electionId: Long): LocalDateTime? {
        return repository.findLatestTimeByElectionId(electionId)
    }
}

interface JpaVoteRepository : JpaRepository<Vote, Long> {
    fun findByElectionIdAndAccountId(electionId: Long, accountId: Long): Vote?

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND v.selectedCandidateId = :candidateId")
    fun countVotesByElectionIdAndCandidateId(electionId: Long, candidateId: Long): Long

    @Query("SELECT MAX(COALESCE(v.updatedAt, v.createdAt)) FROM Vote v WHERE v.electionId = :electionId")
    fun findLatestTimeByElectionId(electionId: Long): LocalDateTime?

    @Query(
        """
        SELECT v FROM Vote v
        WHERE v.electionId = :electionId
        AND (:lastVoteId IS NULL OR v.id > :lastVoteId)
        ORDER BY v.id ASC
    """
    )
    fun findByElectionIdAfterId(
        electionId: Long,
        lastVoteId: Long? = null,
    ): List<Vote>
}