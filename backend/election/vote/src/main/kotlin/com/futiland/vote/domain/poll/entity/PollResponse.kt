package com.futiland.vote.domain.poll.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    indexes = [
        Index(name = "idx_poll_account", columnList = "pollId, accountId, deletedAt"),
        Index(name = "idx_poll_session", columnList = "pollId, anonymousSessionId, deletedAt"),
        Index(name = "idx_account_id", columnList = "accountId, id, deletedAt") // No Offset 페이지네이션용
    ]
)
class PollResponse(
    val pollId: Long,
    val accountId: Long?,
    val anonymousSessionId: String? = null,
    val optionId: Long? = null,      // 선택지 ID (SINGLE_CHOICE, MULTIPLE_CHOICE)
    var scoreValue: Int? = null,     // 점수값 (SCORE)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var updatedAt: LocalDateTime? = null
        private set

    var deletedAt: LocalDateTime? = null
        private set

    init {
        require(accountId != null || anonymousSessionId != null) {
            "accountId 또는 anonymousSessionId 중 하나는 반드시 있어야 합니다"
        }
    }

    companion object {
        fun create(
            pollId: Long,
            accountId: Long?,
            anonymousSessionId: String? = null,
            optionId: Long? = null,
            scoreValue: Int? = null,
        ): PollResponse {
            return PollResponse(
                pollId = pollId,
                accountId = accountId,
                anonymousSessionId = anonymousSessionId,
                optionId = optionId,
                scoreValue = scoreValue
            )
        }
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }
}

fun List<PollResponse>.deleteAll() {
    this.forEach { it.delete() }
}
