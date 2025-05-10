package com.futiland.vote.application.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.common.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*


@Service
class JwtTokenProviderImpl(
    @Value("\${jwt.secret-key}")
    private val secretKey: String
) : JwtTokenProvider {

    override fun generateToken(payload: Map<String, Any>, ttl: Int): String {
        val algorithm: Algorithm = Algorithm.HMAC256(secretKey)

        return JWT.create()
            .withPayload(payload)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + ttl))
            .sign(algorithm)
    }

    override fun parseAuthorizationToken(token: String): AccountJwtPayload {
        val decodedJWT = verifyToken(token)
        val accountId = decodedJWT.getClaim("accountId").asLong()
        return AccountJwtPayload(
            accountId = accountId,
        )
    }

    override fun validateToken(token: String): Boolean {
        return try {
            verifyToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun verifyToken(token: String): DecodedJWT {
        val algorithm = Algorithm.HMAC256(secretKey)
        val verifier = JWT.require(algorithm).build()
        return verifier.verify(token)
    }
}