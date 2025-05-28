package com.futiland.vote.application.account.port.out

import com.futiland.vote.domain.account.dto.response.VerificationResponse
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class IdentityVerificationPortImpl(
    private val portOneIdentityVerificationFeignClient:PortOneIdentityVerificationFeignClient,
    @Value("\${portone.store-id}") private val storeId: String,
    @Value("\${portone.auth-token}") private val authToken: String
):IdentityVerificationPort {
    override fun verify(identityVerificationId: String): VerificationResponse {
        return portOneIdentityVerificationFeignClient.verify(
            identityVerificationId= identityVerificationId,
            storeId= storeId,
            authToken = authToken,
        )
    }
}