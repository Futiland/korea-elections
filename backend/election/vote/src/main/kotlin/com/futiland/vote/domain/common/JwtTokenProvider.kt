package com.futiland.vote.domain.common

import com.futiland.vote.domain.account.dto.AccountJwtPayload

interface JwtTokenProvider {
    fun parseAuthorizationToken(token: String): AccountJwtPayload

    fun validateToken(token: String): Boolean
    fun generateToken(payload: Map<String, Any>, ttl: Int): String
}