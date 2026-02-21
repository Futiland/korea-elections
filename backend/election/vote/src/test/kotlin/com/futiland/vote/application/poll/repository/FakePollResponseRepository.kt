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

    override fun saveAll(pollResponses: List<PollResponse>): List<PollResponse> {
        return pollResponses.map { save(it) }
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

    override fun countDistinctParticipantsByPollId(pollId: Long): Long {
        val activeResponses = responses.values.filter { it.pollId == pollId && it.deletedAt == null }
        val accountCount = activeResponses.mapNotNull { it.accountId }.distinct().count()
        val sessionCount = activeResponses.mapNotNull { it.anonymousSessionId }.distinct().count()
        return (accountCount + sessionCount).toLong()
    }

    override fun countByOptionId(optionId: Long): Long {
        return responses.values.count { it.optionId == optionId && it.deletedAt == null }.toLong()
    }

    override fun findAllByPollIdAndAccountId(pollId: Long, accountId: Long): List<PollResponse> {
        return responses.values.filter {
            it.pollId == pollId && it.accountId == accountId && it.deletedAt == null
        }
    }

    override fun findAllByPollIdAndAnonymousSessionId(pollId: Long, anonymousSessionId: String): List<PollResponse> {
        return responses.values.filter {
            it.pollId == pollId && it.anonymousSessionId == anonymousSessionId && it.deletedAt == null
        }
    }

    override fun findParticipatedPollsByAccountId(accountId: Long, lastId: Long?, size: Int): List<PollResponse> {
        val filtered = responses.values
            .filter { it.accountId == accountId && it.deletedAt == null }
            .sortedByDescending { it.id }

        val startIndex = if (lastId == null) {
            0
        } else {
            filtered.indexOfFirst { it.id < lastId }.takeIf { it >= 0 } ?: filtered.size
        }

        return filtered.drop(startIndex).take(size)
    }

    override fun countByAccountId(accountId: Long): Long {
        return responses.values.count { it.accountId == accountId && it.deletedAt == null }.toLong()
    }

    override fun findVotedPollIds(accountId: Long, pollIds: List<Long>): Set<Long> {
        return responses.values
            .filter { it.accountId == accountId && it.pollId in pollIds && it.deletedAt == null }
            .map { it.pollId }
            .toSet()
    }

    override fun findVotedPollIdsBySessionId(anonymousSessionId: String, pollIds: List<Long>): Set<Long> {
        return responses.values
            .filter { it.anonymousSessionId == anonymousSessionId && it.pollId in pollIds && it.deletedAt == null }
            .map { it.pollId }
            .toSet()
    }

    fun clear() {
        responses.clear()
        idGenerator.set(1)
    }
}
