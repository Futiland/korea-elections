package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.entity.Election

interface ElectionRepository {
    fun getById(id: Long): Election
    fun save(election: Election): Election
    fun findAllByStatus(): List<Election>
}