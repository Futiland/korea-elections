package com.futiland.vote.domain.poll.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["responseId", "optionId"])
    ]
)
class PollResponseOption(
    val responseId: Long,
    val optionId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    companion object {
        fun create(
            responseId: Long,
            optionId: Long,
        ): PollResponseOption {
            return PollResponseOption(
                responseId = responseId,
                optionId = optionId
            )
        }
    }
}
