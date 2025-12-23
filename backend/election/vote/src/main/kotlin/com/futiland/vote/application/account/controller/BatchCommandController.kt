package com.futiland.vote.application.account.controller

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.domain.account.service.AccountBatchEncryptUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/account/v1/batch"])
class BatchCommandController(
    private val accountBatchEncryptUseCase: AccountBatchEncryptUseCase
) {
    @PostMapping("/encrypt-all-accounts")
    fun encryptAllAccounts() : HttpApiResponse<Void> {
        accountBatchEncryptUseCase.encryptAllAccounts()
        return HttpApiResponse.ok()
    }
}