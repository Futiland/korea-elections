package com.futiland.vote.domain.account.service

import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountBatchEncryptService(
    private val accountRepository: AccountRepository,
) : AccountBatchEncryptUseCase {
    @Transactional
    override fun encryptAllAccounts(pageSize: Int) {
        var page = 0
        var accounts: List<Account>
        do {
            accounts = accountRepository.findAllPaged(page, pageSize)
            accounts.forEach { account ->
                val migrateAccount = Account.migrateData(account)
                accountRepository.save(migrateAccount)
            }
            page++
        } while (accounts.isNotEmpty())
    }
}