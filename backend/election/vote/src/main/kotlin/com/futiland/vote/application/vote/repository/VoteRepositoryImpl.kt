package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Vote
import com.futiland.vote.domain.vote.repository.VoteRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class VoteRepositoryImpl(
    private val repository: JpaVoteRepository
): VoteRepository {
    override fun findByElectionIdAndAccountId(electionId: Long, accountId: Long): Vote? {
        return repository.findByElectionIdAndAccountId(electionId, accountId)?.let { return it }
    }

    override fun save(vote: Vote): Vote {
        return repository.save(vote)
    }
}

interface JpaVoteRepository: JpaRepository<Vote, Long> {
    fun findByElectionIdAndAccountId(electionId: Long, accountId: Long): Vote?
}