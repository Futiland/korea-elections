package com.futiland.vote.domain.account.service

interface AccountBatchEncryptUseCase {
    fun encryptAllAccounts(pageSize: Int = 1000)
}