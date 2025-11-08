package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponse
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import java.util.concurrent.atomic.AtomicLong

class FakePollResponseRepository : PollResponseRepository {
    private val responses = mutableMapOf<Long, PollResponse>()
    private val idGenerator = AtomicLong(1)

    override fun save(pollResponse: PollResponse): PollResponse {
        val id = if (pollResponse.id == 0L) idGenerator.getAndIncrement() else pollResponse.id
        val savedResponse = pollResponse.apply {
            javaClass.getDeclaredField("id").apply {
                isAccessible = true
                set(pollResponse, id)
            }
        }
        responses[id] = savedResponse
        return savedResponse
    }

    override fun findById(id: Long): PollResponse? {
        return responses[id]
    }

    override fun findByPollIdAndAccountId(pollId: Long, accountId: Long): PollResponse? {
        return responses.values.find {
            it.pollId == pollId && it.accountId == accountId && it.deletedAt == null
        }
    }

    override fun findAllByPollId(pollId: Long): List<PollResponse> {
        return responses.values.filter { it.pollId == pollId && it.deletedAt == null }
    }

    override fun countByPollId(pollId: Long): Long {
        return responses.values.count { it.pollId == pollId && it.deletedAt == null }.toLong()
    }

    fun clear() {
        responses.clear()
        idGenerator.set(1)
    }
}
