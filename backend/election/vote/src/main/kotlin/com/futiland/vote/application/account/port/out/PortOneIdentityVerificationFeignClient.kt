package com.futiland.vote.application.account.port.out

import com.futiland.vote.domain.account.dto.response.VerificationResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "identity-verification", url = "https://api.portone.io/identity-verifications")
interface PortOneIdentityVerificationFeignClient {
    @GetMapping("/{identityVerificationId}", consumes = ["application/json"])
    fun verify(
        @PathVariable identityVerificationId: String,
        @RequestParam storeId: String, // Default storeId, can be overridden if needed
        @RequestHeader("Authorization") authToken: String,
    ): VerificationResponse
}