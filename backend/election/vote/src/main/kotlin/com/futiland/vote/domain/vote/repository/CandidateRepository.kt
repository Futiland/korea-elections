package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.entity.Candidate

interface CandidateRepository {
    fun saveAll(candidates: List<Candidate>): List<Candidate>
}