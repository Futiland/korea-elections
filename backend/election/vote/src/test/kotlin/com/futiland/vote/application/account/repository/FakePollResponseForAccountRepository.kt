package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.PollTypeForAccount
import com.futiland.vote.domain.account.repository.PollResponseForAccountRepository

class FakePollResponseForAccountRepository : PollResponseForAccountRepository {

    var participatedPublicPollCount: Long = 0L
    var participatedSystemPollCount: Long = 0L

    override fun countParticipatedPollsByPollType(accountId: Long, pollType: PollTypeForAccount): Long {
        return when (pollType) {
            PollTypeForAccount.PUBLIC -> participatedPublicPollCount
            PollTypeForAccount.SYSTEM -> participatedSystemPollCount
        }
    }
}
