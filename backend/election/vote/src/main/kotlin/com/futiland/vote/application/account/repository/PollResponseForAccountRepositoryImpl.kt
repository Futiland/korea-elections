package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.PollResponseForAccount
import com.futiland.vote.domain.account.entity.PollStatusForAccount
import com.futiland.vote.domain.account.entity.PollTypeForAccount
import com.futiland.vote.domain.account.repository.PollResponseForAccountRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
class PollResponseForAccountRepositoryImpl(
    private val repository: JpaPollResponseForAccountRepository
) : PollResponseForAccountRepository {

    override fun countParticipatedPollsByPollType(accountId: Long, pollType: PollTypeForAccount): Long {
        return repository.countParticipatedPollsByPollType(accountId, pollType, PollStatusForAccount.DELETED)
    }
}

interface JpaPollResponseForAccountRepository : JpaRepository<PollResponseForAccount, Long> {

    @Query("""
        SELECT COUNT(DISTINCT pr.pollId) FROM PollResponseForAccount pr
        JOIN PollForAccount p ON pr.pollId = p.id
        WHERE pr.accountId = :accountId
        AND p.pollType = :pollType
        AND p.status != :excludeStatus
        AND pr.deletedAt IS NULL
    """)
    fun countParticipatedPollsByPollType(accountId: Long, pollType: PollTypeForAccount, excludeStatus: PollStatusForAccount): Long
}
