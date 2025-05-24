package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.entity.Vote

interface VoteRepository {
    fun findByElectionIdAndAccountId(electionId: Long, accountId: Long): Vote?
    fun save(vote: Vote): Vote
    fun findByElectionIdAndCandidateId(electionId: Long, candidateId: Long): Long
}