package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponse
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class PollResponseRepositoryImpl(
    private val repository: JpaPollResponseRepository
) : PollResponseRepository {
    override fun save(pollResponse: PollResponse): PollResponse {
        return repository.save(pollResponse)
    }

    override fun saveAll(pollResponses: List<PollResponse>): List<PollResponse> {
        return repository.saveAll(pollResponses)
    }

    override fun findById(id: Long): PollResponse? {
        return repository.findById(id).orElse(null)
    }

    override fun findByPollIdAndAccountId(pollId: Long, accountId: Long): PollResponse? {
        return repository.findByPollIdAndAccountIdAndDeletedAtIsNull(pollId, accountId)
    }

    override fun findAllByPollIdAndAccountId(pollId: Long, accountId: Long): List<PollResponse> {
        return repository.findAllByPollIdAndAccountIdAndDeletedAtIsNull(pollId, accountId)
    }

    override fun findAllByPollId(pollId: Long): List<PollResponse> {
        return repository.findAllByPollIdAndDeletedAtIsNull(pollId)
    }

    override fun countByPollId(pollId: Long): Long {
        return repository.countByPollIdAndDeletedAtIsNull(pollId)
    }

    override fun countByOptionId(optionId: Long): Long {
        return repository.countByOptionIdAndDeletedAtIsNull(optionId)
    }
}

interface JpaPollResponseRepository : JpaRepository<PollResponse, Long> {
    fun findByPollIdAndAccountIdAndDeletedAtIsNull(pollId: Long, accountId: Long): PollResponse?
    fun findAllByPollIdAndAccountIdAndDeletedAtIsNull(pollId: Long, accountId: Long): List<PollResponse>
    fun findAllByPollIdAndDeletedAtIsNull(pollId: Long): List<PollResponse>
    fun countByPollIdAndDeletedAtIsNull(pollId: Long): Long
    fun countByOptionIdAndDeletedAtIsNull(optionId: Long): Long
}
