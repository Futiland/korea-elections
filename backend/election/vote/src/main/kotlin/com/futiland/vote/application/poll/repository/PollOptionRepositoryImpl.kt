package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollOption
import com.futiland.vote.domain.poll.repository.PollOptionRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class PollOptionRepositoryImpl(
    private val repository: JpaPollOptionRepository
) : PollOptionRepository {
    override fun save(pollOption: PollOption): PollOption {
        return repository.save(pollOption)
    }

    override fun saveAll(pollOptions: List<PollOption>): List<PollOption> {
        return repository.saveAll(pollOptions)
    }

    override fun findById(id: Long): PollOption? {
        return repository.findById(id).orElse(null)
    }

    override fun findAllByPollId(pollId: Long): List<PollOption> {
        return repository.findAllByPollIdAndDeletedAtIsNullOrderByOptionOrder(pollId)
    }

    override fun findAllByPollIdIn(pollIds: List<Long>): List<PollOption> {
        return repository.findAllByPollIdInAndDeletedAtIsNullOrderByOptionOrder(pollIds)
    }

    override fun deleteAllByPollId(pollId: Long) {
        repository.deleteAllByPollId(pollId)
    }
}

interface JpaPollOptionRepository : JpaRepository<PollOption, Long> {
    fun findAllByPollIdAndDeletedAtIsNullOrderByOptionOrder(pollId: Long): List<PollOption>
    fun findAllByPollIdInAndDeletedAtIsNullOrderByOptionOrder(pollIds: List<Long>): List<PollOption>
    fun deleteAllByPollId(pollId: Long)
}
