package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.entity.Candidate

interface CandidateRepository {
    fun saveAll(candidates: List<Candidate>): List<Candidate>
    fun findByIds(ids: List<Long>): List<Candidate>
    fun findByElectionId(electionId: Long): List<Candidate>
}