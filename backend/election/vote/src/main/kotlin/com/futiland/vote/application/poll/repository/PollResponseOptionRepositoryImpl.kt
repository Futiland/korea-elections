package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponseOption
import com.futiland.vote.domain.poll.repository.PollResponseOptionRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class PollResponseOptionRepositoryImpl(
    private val repository: JpaPollResponseOptionRepository
) : PollResponseOptionRepository {
    override fun save(pollResponseOption: PollResponseOption): PollResponseOption {
        return repository.save(pollResponseOption)
    }

    override fun saveAll(pollResponseOptions: List<PollResponseOption>): List<PollResponseOption> {
        return repository.saveAll(pollResponseOptions)
    }

    override fun findAllByResponseId(responseId: Long): List<PollResponseOption> {
        return repository.findAllByResponseId(responseId)
    }

    override fun deleteAllByResponseId(responseId: Long) {
        repository.deleteAllByResponseId(responseId)
    }

    override fun countByOptionId(optionId: Long): Long {
        return repository.countByOptionId(optionId)
    }
}

interface JpaPollResponseOptionRepository : JpaRepository<PollResponseOption, Long> {
    fun findAllByResponseId(responseId: Long): List<PollResponseOption>
    fun deleteAllByResponseId(responseId: Long)
    fun countByOptionId(optionId: Long): Long
}
