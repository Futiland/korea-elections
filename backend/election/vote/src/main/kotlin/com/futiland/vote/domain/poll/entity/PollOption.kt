package com.futiland.vote.domain.poll.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class PollOption(
    val pollId: Long,
    var optionText: String,
    var optionOrder: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun create(
            pollId: Long,
            optionText: String,
            optionOrder: Int,
        ): PollOption {
            return PollOption(
                pollId = pollId,
                optionText = optionText,
                optionOrder = optionOrder
            )
        }
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
