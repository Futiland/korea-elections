package com.futiland.vote.domain.vote.repository

import com.futiland.vote.domain.vote.entity.Party

interface PartyRepository {
    fun findById(id: Long): Party?
    fun save(party: Party): Party
}