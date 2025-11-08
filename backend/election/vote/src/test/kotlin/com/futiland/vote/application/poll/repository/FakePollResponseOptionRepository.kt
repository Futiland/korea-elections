package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponseOption
import com.futiland.vote.domain.poll.repository.PollResponseOptionRepository
import java.util.concurrent.atomic.AtomicLong

class FakePollResponseOptionRepository : PollResponseOptionRepository {
    private val responseOptions = mutableMapOf<Long, PollResponseOption>()
    private val idGenerator = AtomicLong(1)

    override fun save(pollResponseOption: PollResponseOption): PollResponseOption {
        val id = if (pollResponseOption.id == 0L) idGenerator.getAndIncrement() else pollResponseOption.id
        val saved = pollResponseOption.apply {
            javaClass.getDeclaredField("id").apply {
                isAccessible = true
                set(pollResponseOption, id)
            }
        }
        responseOptions[id] = saved
        return saved
    }

    override fun saveAll(pollResponseOptions: List<PollResponseOption>): List<PollResponseOption> {
        return pollResponseOptions.map { save(it) }
    }

    override fun findAllByResponseId(responseId: Long): List<PollResponseOption> {
        return responseOptions.values.filter { it.responseId == responseId }
    }

    override fun deleteAllByResponseId(responseId: Long) {
        responseOptions.entries.removeIf { it.value.responseId == responseId }
    }

    override fun countByOptionId(optionId: Long): Long {
        return responseOptions.values.count { it.optionId == optionId }.toLong()
    }

    fun clear() {
        responseOptions.clear()
        idGenerator.set(1)
    }
}
