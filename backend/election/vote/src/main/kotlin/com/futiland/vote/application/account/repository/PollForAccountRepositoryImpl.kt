package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.PollForAccount
import com.futiland.vote.domain.account.repository.PollForAccountRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
class PollForAccountRepositoryImpl(
    private val repository: JpaPollForAccountRepository
) : PollForAccountRepository {

    override fun countByCreatorAccountId(accountId: Long): Long {
        return repository.countByCreatorAccountId(accountId)
    }
}

interface JpaPollForAccountRepository : JpaRepository<PollForAccount, Long> {

    @Query("""
        SELECT COUNT(p) FROM PollForAccount p
        WHERE p.creatorAccountId = :accountId
        AND p.status != 'DELETED'
    """)
    fun countByCreatorAccountId(accountId: Long): Long
}
