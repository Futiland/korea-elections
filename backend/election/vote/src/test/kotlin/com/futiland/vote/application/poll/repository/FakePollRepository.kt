package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.util.SliceContent
import java.util.concurrent.atomic.AtomicLong

class FakePollRepository : PollRepository {
    private val polls = mutableMapOf<Long, Poll>()
    private val idGenerator = AtomicLong(1)

    override fun save(poll: Poll): Poll {
        val id = if (poll.id == 0L) idGenerator.getAndIncrement() else poll.id
        val savedPoll = poll.apply {
            javaClass.getDeclaredField("id").apply {
                isAccessible = true
                set(poll, id)
            }
        }
        polls[id] = savedPoll
        return savedPoll
    }

    override fun getById(id: Long): Poll {
        return findById(id) ?: throw IllegalArgumentException("여론조사를 찾을 수 없습니다.")
    }

    override fun findById(id: Long): Poll? {
        return polls[id]
    }

    override fun findAllPublicDisplayable(size: Int, nextCursor: String?): SliceContent<Poll> {
        // 공개 표시 가능한 여론조사: IN_PROGRESS, EXPIRED 상태만
        val displayableStatuses = listOf(PollStatus.IN_PROGRESS, PollStatus.EXPIRED)
        val filteredPolls = polls.values
            .filter { it.status in displayableStatuses }
            .sortedByDescending { it.id }

        val startIndex = if (nextCursor == null) {
            0
        } else {
            val cursorId = nextCursor.toLong()
            filteredPolls.indexOfFirst { it.id < cursorId }.takeIf { it >= 0 } ?: filteredPolls.size
        }

        val content = filteredPolls.drop(startIndex).take(size)
        val cursor = if (content.size < size) null else content.lastOrNull()?.id?.toString()

        return SliceContent(content, cursor)
    }

    override fun findAllByCreatorAccountId(creatorAccountId: Long): List<Poll> {
        return polls.values.filter { it.creatorAccountId == creatorAccountId }
    }

    override fun findAllByIdIn(ids: List<Long>): List<Poll> {
        return polls.values.filter { it.id in ids }
    }

    override fun findMyPolls(creatorAccountId: Long, size: Int, lastId: Long?): SliceContent<Poll> {
        val filteredPolls = polls.values
            .filter { it.creatorAccountId == creatorAccountId }
            .sortedByDescending { it.id }

        val startIndex = if (lastId == null) {
            0
        } else {
            filteredPolls.indexOfFirst { it.id < lastId }.takeIf { it >= 0 } ?: filteredPolls.size
        }

        val content = filteredPolls.drop(startIndex).take(size)
        val cursor = if (content.size < size) null else content.lastOrNull()?.id?.toString()

        return SliceContent(content, cursor)
    }

    fun clear() {
        polls.clear()
        idGenerator.set(1)
    }
}
