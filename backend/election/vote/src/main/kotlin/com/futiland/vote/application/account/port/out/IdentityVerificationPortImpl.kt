package com.futiland.vote.application.account.port.out

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.dto.response.VerificationResponse
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class IdentityVerificationPortImpl(
    private val portOneIdentityVerificationFeignClient: PortOneIdentityVerificationFeignClient,
    @Value("\${portone.store-id}") private val storeId: String,
    @Value("\${portone.auth-token}") private val authToken: String
) : IdentityVerificationPort {
    override fun verify(identityVerificationId: String): VerificationResponse {
        try {
            return portOneIdentityVerificationFeignClient.verify(
                identityVerificationId = identityVerificationId,
                storeId = storeId,
                authToken = authToken,
            )
        } catch (e: Exception) {
            throw ApplicationException(
                code = CodeEnum.FRS_004,
                "본인인증에 실패했습니다. 잘못된 경로로 시도하셨습니다."
            )
        }
    }
}