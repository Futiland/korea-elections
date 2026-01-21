package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollOption
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import java.util.concurrent.atomic.AtomicLong

class FakePollOptionRepository : PollOptionRepository {
    private val options = mutableMapOf<Long, PollOption>()
    private val idGenerator = AtomicLong(1)

    override fun save(pollOption: PollOption): PollOption {
        val id = if (pollOption.id == 0L) idGenerator.getAndIncrement() else pollOption.id
        val savedOption = pollOption.apply {
            javaClass.getDeclaredField("id").apply {
                isAccessible = true
                set(pollOption, id)
            }
        }
        options[id] = savedOption
        return savedOption
    }

    override fun saveAll(pollOptions: List<PollOption>): List<PollOption> {
        return pollOptions.map { save(it) }
    }

    override fun findById(id: Long): PollOption? {
        return options[id]
    }

    override fun findAllByPollId(pollId: Long): List<PollOption> {
        return options.values
            .filter { it.pollId == pollId && it.deletedAt == null }
            .sortedBy { it.optionOrder }
    }

    override fun deleteAllByPollId(pollId: Long) {
        options.entries.removeIf { it.value.pollId == pollId }
    }

    override fun findAllByPollIdIn(pollIds: List<Long>): List<PollOption> {
        return options.values
            .filter { it.pollId in pollIds && it.deletedAt == null }
            .sortedBy { it.optionOrder }
    }

    override fun findAllByPollIdAndIdIn(pollId: Long, optionIds: List<Long>): List<PollOption> {
        return options.values
            .filter { it.pollId == pollId && it.id in optionIds && it.deletedAt == null }
    }

    fun clear() {
        options.clear()
        idGenerator.set(1)
    }
}
