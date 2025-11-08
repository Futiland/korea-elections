package com.futiland.vote.domain.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponseOption

interface PollResponseOptionRepository {
    fun save(pollResponseOption: PollResponseOption): PollResponseOption
    fun saveAll(pollResponseOptions: List<PollResponseOption>): List<PollResponseOption>
    fun findAllByResponseId(responseId: Long): List<PollResponseOption>
    fun deleteAllByResponseId(responseId: Long)
    fun countByOptionId(optionId: Long): Long
}
