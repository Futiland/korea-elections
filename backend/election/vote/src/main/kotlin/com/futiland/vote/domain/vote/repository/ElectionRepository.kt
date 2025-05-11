package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.dto.election.ElectionDto
import com.futiland.vote.domain.vote.entity.Election
import com.futiland.vote.util.SliceContent

interface ElectionRepository {
    fun getById(id: Long): Election
    fun save(election: Election): Election
    fun findAllByStatus(): List<Election>
    fun findAll(size: Int, nextCursor: String?): SliceContent<Election>
}