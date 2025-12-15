package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import java.time.LocalDateTime
import kotlin.math.min
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

    override fun findAllPublicDisplayable(
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        val filteredPolls = polls.values
            .filter { it.status in statusFilter.statuses }
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

    override fun findAllSystemDisplayable(
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        val filteredPolls = polls.values
            .filter { it.status in statusFilter.statuses }
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

    override fun findAllByIdIn(ids: List<Long>): List<Poll> {
        return polls.values.filter { it.id in ids && it.status != PollStatus.DELETED }
    }

    override fun findMyPollsWithPage(creatorAccountId: Long, page: Int, size: Int): PageContent<Poll> {
        val filteredPolls = polls.values
            .filter { it.creatorAccountId == creatorAccountId && it.status != PollStatus.DELETED }
            .sortedByDescending { it.id }

        val totalCount = filteredPolls.size.toLong()
        val startIndex = (page - 1) * size
        val endIndex = min(startIndex + size, filteredPolls.size)
        val content = if (startIndex < filteredPolls.size) {
            filteredPolls.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PageContent.of(content, totalCount, size)
    }

    override fun expireOverduePolls(now: LocalDateTime): Int {
        val pollsToExpire = polls.values.filter {
            it.status == PollStatus.IN_PROGRESS && it.endAt != null && it.endAt!! < now
        }

        pollsToExpire.forEach { poll ->
            poll.expire()
        }

        return pollsToExpire.size
    }

    override fun searchPublicPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        return searchByKeywordAndType(keyword, PollType.PUBLIC, size, nextCursor, statusFilter)
    }

    override fun searchSystemPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        return searchByKeywordAndType(keyword, PollType.SYSTEM, size, nextCursor, statusFilter)
    }

    override fun searchAllPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        val filteredPolls = polls.values
            .filter { it.status in statusFilter.statuses }
            .filter { it.title.contains(keyword) || it.description.contains(keyword) }
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

    private fun searchByKeywordAndType(
        keyword: String,
        pollType: PollType,
        size: Int,
        nextCursor: String?,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        val filteredPolls = polls.values
            .filter { it.pollType == pollType && it.status in statusFilter.statuses }
            .filter { it.title.contains(keyword) || it.description.contains(keyword) }
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

    fun clear() {
        polls.clear()
        idGenerator.set(1)
    }
}
