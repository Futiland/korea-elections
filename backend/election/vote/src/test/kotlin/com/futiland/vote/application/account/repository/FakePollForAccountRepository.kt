package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.repository.PollForAccountRepository

class FakePollForAccountRepository : PollForAccountRepository {

    var createdPollCount: Long = 0L

    override fun countByCreatorAccountId(accountId: Long): Long {
        return createdPollCount
    }
}
