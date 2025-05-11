package com.futiland.vote.domain.vote.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime

@Entity
class Candidate(
    val electionId: Long,
    val name: String,
    @Comment("후보자 번호")
    val number: Int,
    val description: String,
    status:CandidateStatus,
    val createdAt: LocalDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @Enumerated(EnumType.STRING)
    var status: CandidateStatus = status
        private set

    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun create(
            electionId: Long,
            number: Int,
            name: String,
            description: String,
        ): Candidate {
            return Candidate(
                electionId = electionId,
                number = number,
                name = name,
                description = description,
                status = CandidateStatus.ACTIVE,
                createdAt = LocalDateTime.now(),
            )
        }
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
        this.status = CandidateStatus.INACTIVE
    }
}