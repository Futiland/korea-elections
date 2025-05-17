package com.futiland.vote.domain.vote.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["electionId", "accountId"])
    ]
)
class Vote(
    val electionId: Long,
    selectedCandidateId: Long,
    val accountId: Long,
    val createdAt: LocalDateTime= LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var selectedCandidateId: Long= selectedCandidateId
        private set

    var updatedAt: LocalDateTime? = null
        private set
    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun create(
            selectedCandidateId: Long,
            electionId: Long,
            accountId: Long,
        ): Vote {
            return Vote(
                selectedCandidateId= selectedCandidateId,
                electionId = electionId,
                accountId = accountId,
            )
        }
    }

    fun update(
        selectedCandidateId: Long,
    ) {
        this.selectedCandidateId = selectedCandidateId
        this.updatedAt = LocalDateTime.now()
    }
}