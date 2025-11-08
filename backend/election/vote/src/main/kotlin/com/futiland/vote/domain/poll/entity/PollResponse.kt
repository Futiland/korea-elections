package com.futiland.vote.domain.poll.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["pollId", "accountId", "deletedAt"])
    ]
)
class PollResponse(
    val pollId: Long,
    val accountId: Long,
    var scoreValue: Int? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var updatedAt: LocalDateTime? = null
        private set

    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun create(
            pollId: Long,
            accountId: Long,
            scoreValue: Int? = null,
        ): PollResponse {
            return PollResponse(
                pollId = pollId,
                accountId = accountId,
                scoreValue = scoreValue
            )
        }
    }

    fun updateScore(scoreValue: Int) {
        this.scoreValue = scoreValue
        this.updatedAt = LocalDateTime.now()
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }
}
