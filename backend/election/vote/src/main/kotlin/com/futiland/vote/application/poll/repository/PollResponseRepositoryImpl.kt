package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponse
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
class PollResponseRepositoryImpl(
    private val repository: JpaPollResponseRepository
) : PollResponseRepository {
    override fun save(pollResponse: PollResponse): PollResponse {
        return repository.save(pollResponse)
    }

    override fun saveAll(pollResponses: List<PollResponse>): List<PollResponse> {
        return repository.saveAll(pollResponses)
    }

    override fun findById(id: Long): PollResponse? {
        return repository.findById(id).orElse(null)
    }

    override fun findByPollIdAndAccountId(pollId: Long, accountId: Long): PollResponse? {
        return repository.findByPollIdAndAccountIdAndDeletedAtIsNull(pollId, accountId)
    }

    override fun findAllByPollIdAndAccountId(pollId: Long, accountId: Long): List<PollResponse> {
        return repository.findAllByPollIdAndAccountIdAndDeletedAtIsNull(pollId, accountId)
    }

    override fun findAllByPollId(pollId: Long): List<PollResponse> {
        return repository.findAllByPollIdAndDeletedAtIsNull(pollId)
    }

    override fun countByPollId(pollId: Long): Long {
        return repository.countByPollIdAndDeletedAtIsNull(pollId)
    }

    override fun countByOptionId(optionId: Long): Long {
        return repository.countByOptionIdAndDeletedAtIsNull(optionId)
    }

    override fun findParticipatedPollsByAccountId(accountId: Long, lastId: Long?, size: Int): List<PollResponse> {
        return if (lastId == null) {
            // 첫 페이지: 최신순으로 조회
            repository.findByAccountIdAndDeletedAtIsNullOrderByIdDesc(accountId, PageRequest.of(0, size))
        } else {
            // 다음 페이지: lastId보다 작은 id를 조회 (최신순)
            repository.findByAccountIdAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(accountId, lastId, PageRequest.of(0, size))
        }
    }

    override fun countByAccountId(accountId: Long): Long {
        return repository.countByAccountIdAndDeletedAtIsNull(accountId)
    }

    override fun findVotedPollIds(accountId: Long, pollIds: List<Long>): Set<Long> {
        if (pollIds.isEmpty()) return emptySet()
        return repository.findPollIdsByAccountIdAndPollIdIn(accountId, pollIds).toSet()
    }
}

interface JpaPollResponseRepository : JpaRepository<PollResponse, Long> {
    fun findByPollIdAndAccountIdAndDeletedAtIsNull(pollId: Long, accountId: Long): PollResponse?
    fun findAllByPollIdAndAccountIdAndDeletedAtIsNull(pollId: Long, accountId: Long): List<PollResponse>
    fun findAllByPollIdAndDeletedAtIsNull(pollId: Long): List<PollResponse>
    fun countByPollIdAndDeletedAtIsNull(pollId: Long): Long
    fun countByOptionIdAndDeletedAtIsNull(optionId: Long): Long

    // No Offset 페이지네이션 (커버링 인덱스: idx_account_id)
    @Query("""
        SELECT pr FROM PollResponse pr
        WHERE pr.accountId = :accountId
        AND pr.deletedAt IS NULL
        ORDER BY pr.id DESC
    """)
    fun findByAccountIdAndDeletedAtIsNullOrderByIdDesc(
        accountId: Long,
        pageable: Pageable
    ): List<PollResponse>

    @Query("""
        SELECT pr FROM PollResponse pr
        WHERE pr.accountId = :accountId
        AND pr.id < :id
        AND pr.deletedAt IS NULL
        ORDER BY pr.id DESC
    """)
    fun findByAccountIdAndIdLessThanAndDeletedAtIsNullOrderByIdDesc(
        accountId: Long,
        id: Long,
        pageable: Pageable
    ): List<PollResponse>

    @Query("""
        SELECT COUNT(pr) FROM PollResponse pr
        WHERE pr.accountId = :accountId
        AND pr.deletedAt IS NULL
    """)
    fun countByAccountIdAndDeletedAtIsNull(accountId: Long): Long

    @Query("""
        SELECT DISTINCT pr.pollId FROM PollResponse pr
        WHERE pr.accountId = :accountId
        AND pr.pollId IN :pollIds
        AND pr.deletedAt IS NULL
    """)
    fun findPollIdsByAccountIdAndPollIdIn(accountId: Long, pollIds: List<Long>): List<Long>
}
