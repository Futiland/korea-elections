package com.futiland.vote.application.poll.repository

import com.futiland.vote.application.poll.dto.response.CreatorInfoResponse
import com.futiland.vote.domain.poll.entity.AccountForPoll
import com.futiland.vote.domain.poll.repository.AccountForPollRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
class AccountForPollRepositoryImpl(
    private val repository: JpaAccountForPollRepository
) : AccountForPollRepository {

    override fun getCreatorInfoById(accountId: Long): CreatorInfoResponse {
        val account = repository.findById(accountId).orElseThrow {
            IllegalArgumentException("계정을 찾을 수 없습니다.")
        }
        return CreatorInfoResponse(account.id, account.name)
    }

    override fun getCreatorInfoByIds(accountIds: List<Long>): Map<Long, CreatorInfoResponse> {
        if (accountIds.isEmpty()) return emptyMap()
        val accounts = repository.findAllByIdIn(accountIds)
        return accounts.associate { account ->
            account.id to CreatorInfoResponse(account.id, account.name)
        }
    }
}

interface JpaAccountForPollRepository : JpaRepository<AccountForPoll, Long> {

    @Query("""
        SELECT a FROM AccountForPoll a
        WHERE a.id IN :ids
    """)
    fun findAllByIdIn(ids: List<Long>): List<AccountForPoll>
}
