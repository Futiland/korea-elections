package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Party
import com.futiland.vote.domain.vote.repository.PartyRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class PartyRepositoryImpl(
    private val repository: JpaPartyRepository
) : PartyRepository{
    override fun findById(id: Long): Party {
        return repository.findById(id).orElse(null)
    }

    override fun save(party: Party): Party {
        return repository.save(party)
    }
}

interface JpaPartyRepository : JpaRepository<Party, Long> {
}
