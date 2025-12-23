package com.futiland.vote.domain.account.repository

import com.futiland.vote.domain.account.entity.PollTypeForAccount

interface PollResponseForAccountRepository {
    fun countParticipatedPollsByPollType(accountId: Long, pollType: PollTypeForAccount): Long
}
