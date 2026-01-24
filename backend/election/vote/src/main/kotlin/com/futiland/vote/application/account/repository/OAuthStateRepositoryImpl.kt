package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.OAuthState
import com.futiland.vote.domain.account.repository.OAuthStateRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class OAuthStateRepositoryImpl(
    private val jpaOAuthStateRepository: JpaOAuthStateRepository
) : OAuthStateRepository {
    override fun save(state: OAuthState) = jpaOAuthStateRepository.save(state)
    override fun findByState(state: String) = jpaOAuthStateRepository.findById(state).orElse(null)
    override fun deleteByState(state: String) = jpaOAuthStateRepository.deleteById(state)
}

interface JpaOAuthStateRepository : JpaRepository<OAuthState, String>
