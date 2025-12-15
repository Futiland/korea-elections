package com.futiland.vote.domain.account.repository

interface PollForAccountRepository {
    fun countByCreatorAccountId(accountId: Long): Long
}
