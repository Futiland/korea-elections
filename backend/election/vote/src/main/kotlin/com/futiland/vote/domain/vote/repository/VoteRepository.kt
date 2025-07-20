package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.entity.Vote
import java.time.LocalDateTime

interface VoteRepository {
    fun findByElectionIdAndAccountId(electionId: Long, accountId: Long): Vote?
    fun findByElectionId(electionId: Long, lastVoteId: Long?, limit: Int): List<Vote>
    fun save(vote: Vote): Vote
    fun findByElectionIdAndCandidateId(electionId: Long, candidateId: Long): Long
    fun findLatestTimeByElectionId(electionId: Long): LocalDateTime?
}