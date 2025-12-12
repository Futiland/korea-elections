package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.repository.PollRepository
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
        // 공개 표시 가능한 여론조사: IN_PROGRESS, EXPIRED 상태만
        val pageable = PageRequest.ofSize(size)
        val displayableStatuses = listOf(PollStatus.IN_PROGRESS, PollStatus.EXPIRED)

        val content = if (nextCursor == null) {
            repository.getPollsFromLatest(
                status = displayableStatuses,
                pageable = pageable
            )
        } else {
            repository.getPollsFromLastId(
                pollId = nextCursor.toLong(),
                status = displayableStatuses,
                pageable = pageable
            )
        }
        val id: String? = if (content.isEmpty() || content.size < size)
            null
        else
            content[content.size - 1].id.toString()
        return SliceContent(content, id)
    }

    override fun findAllByCreatorAccountId(creatorAccountId: Long): List<Poll> {
        return repository.findAllByCreatorAccountId(creatorAccountId)
    }

    override fun findAllByIdIn(ids: List<Long>): List<Poll> {
        return repository.findAllByIdIn(ids)
    }

    override fun findMyPolls(creatorAccountId: Long, size: Int, lastId: Long?): SliceContent<Poll> {
        val pageable = PageRequest.ofSize(size + 1)

        val content = if (lastId == null) {
            repository.getMyPollsFromLatest(creatorAccountId, pageable)
        } else {
            repository.getMyPollsFromLastId(creatorAccountId, lastId, pageable)
        }

        val hasNext = content.size > size
        val polls = if (hasNext) content.dropLast(1) else content
        val nextCursor = if (hasNext) polls.lastOrNull()?.id?.toString() else null

        return SliceContent(polls, nextCursor)
    }

    override fun expireOverduePolls(now: LocalDateTime): Int {
        return repository.expireOverduePolls(now)
    }
}

interface JpaPollRepository : JpaRepository<Poll, Long> {
    fun findAllByCreatorAccountId(creatorAccountId: Long): List<Poll>
    fun findAllByIdIn(ids: List<Long>): List<Poll>

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
        WHERE p.creatorAccountId = :creatorAccountId
        ORDER BY p.id DESC
        """
    )
    fun getMyPollsFromLatest(creatorAccountId: Long, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.creatorAccountId = :creatorAccountId AND p.id < :lastId
        ORDER BY p.id DESC
        """
    )
    fun getMyPollsFromLastId(creatorAccountId: Long, lastId: Long, pageable: PageRequest): List<Poll>

    @Modifying
    @Query(
        """
        UPDATE Poll p
        SET p.status = 'EXPIRED', p.updatedAt = :now
        WHERE p.status = 'IN_PROGRESS'
        AND p.endAt < :now
        """
    )
    fun expireOverduePolls(now: LocalDateTime): Int
}
