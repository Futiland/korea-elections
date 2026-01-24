package com.futiland.vote.domain.account.repository

import com.futiland.vote.domain.account.entity.OAuthState

interface OAuthStateRepository {
    fun save(state: OAuthState): OAuthState
    fun findByState(state: String): OAuthState?
    fun deleteByState(state: String)
}
