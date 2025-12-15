package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PollRepositoryImpl(
    private val repository: JpaPollRepository
) : PollRepository {
    override fun save(poll: Poll): Poll {
        return repository.save(poll)
    }

    override fun getById(id: Long): Poll {
        return repository.findById(id).orElseThrow {
            IllegalArgumentException("여론조사를 찾을 수 없습니다.")
        }
    }

    override fun findById(id: Long): Poll? {
        return repository.findById(id).orElse(null)
    }

    override fun findAllPublicDisplayable(size: Int, nextCursor: String?): SliceContent<Poll> {
        // 공개 표시 가능한 여론조사: IN_PROGRESS, EXPIRED 상태 + PUBLIC 타입만
        val pageable = PageRequest.ofSize(size)
        val displayableStatuses = listOf(PollStatus.IN_PROGRESS, PollStatus.EXPIRED)

        val content = if (nextCursor == null) {
            repository.getPollsFromLatestByType(
                status = displayableStatuses,
                pollType = PollType.PUBLIC,
                pageable = pageable
            )
        } else {
            repository.getPollsFromLastIdByType(
                pollId = nextCursor.toLong(),
                status = displayableStatuses,
                pollType = PollType.PUBLIC,
                pageable = pageable
            )
        }
        val id: String? = if (content.isEmpty() || content.size < size)
            null
        else
            content[content.size - 1].id.toString()
        return SliceContent(content, id)
    }

    override fun findAllSystemDisplayable(size: Int, nextCursor: String?): SliceContent<Poll> {
        // 시스템 여론조사: IN_PROGRESS, EXPIRED 상태 + SYSTEM 타입만
        val pageable = PageRequest.ofSize(size)
        val displayableStatuses = listOf(PollStatus.IN_PROGRESS, PollStatus.EXPIRED)

        val content = if (nextCursor == null) {
            repository.getPollsFromLatestByType(
                status = displayableStatuses,
                pollType = PollType.SYSTEM,
                pageable = pageable
            )
        } else {
            repository.getPollsFromLastIdByType(
                pollId = nextCursor.toLong(),
                status = displayableStatuses,
                pollType = PollType.SYSTEM,
                pageable = pageable
            )
        }
        val id: String? = if (content.isEmpty() || content.size < size)
            null
        else
            content[content.size - 1].id.toString()
        return SliceContent(content, id)
    }

    override fun findAllByIdIn(ids: List<Long>): List<Poll> {
        if (ids.isEmpty()) return emptyList()
        return repository.findAllByIdInExcludeStatus(ids, PollStatus.DELETED)
    }

    override fun findMyPollsWithPage(creatorAccountId: Long, page: Int, size: Int): PageContent<Poll> {
        val pageable = PageRequest.of(page - 1, size)
        val content = repository.findByCreatorAccountIdExcludeStatus(creatorAccountId, PollStatus.DELETED, pageable)
        val totalCount = repository.countByCreatorAccountIdExcludeStatus(creatorAccountId, PollStatus.DELETED)
        return PageContent.of(content, totalCount, size)
    }

    override fun expireOverduePolls(now: LocalDateTime): Int {
        return repository.expireOverduePolls(now, PollStatus.IN_PROGRESS, PollStatus.EXPIRED)
    }
}

interface JpaPollRepository : JpaRepository<Poll, Long> {

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id IN :ids
        AND p.status != :excludeStatus
        """
    )
    fun findAllByIdInExcludeStatus(ids: List<Long>, excludeStatus: PollStatus): List<Poll>

    @Query(
        """
        SELECT COUNT(p) FROM Poll p
        WHERE p.creatorAccountId = :creatorAccountId
        AND p.status != :excludeStatus
        """
    )
    fun countByCreatorAccountIdExcludeStatus(creatorAccountId: Long, excludeStatus: PollStatus): Long

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.creatorAccountId = :creatorAccountId
        AND p.status != :excludeStatus
        ORDER BY p.id DESC
        """
    )
    fun findByCreatorAccountIdExcludeStatus(creatorAccountId: Long, excludeStatus: PollStatus, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status in :status
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLatest(status: List<PollStatus>, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id < :pollId and p.status in :status
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLastId(
        pollId: Long,
        status: List<PollStatus>,
        pageable: PageRequest
    ): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status in :status AND p.pollType = :pollType
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLatestByType(status: List<PollStatus>, pollType: PollType, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id < :pollId AND p.status in :status AND p.pollType = :pollType
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLastIdByType(
        pollId: Long,
        status: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    @Modifying
    @Query(
        """
        UPDATE Poll p
        SET p.status = :expiredStatus, p.updatedAt = :now
        WHERE p.status = :inProgressStatus
        AND p.endAt < :now
        """
    )
    fun expireOverduePolls(now: LocalDateTime, inProgressStatus: PollStatus, expiredStatus: PollStatus): Int
}
