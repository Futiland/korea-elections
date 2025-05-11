package com.futiland.vote.domain.vote.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Election(
    val title: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val description: String,
    initialStatus: ElectionStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    var status: ElectionStatus = determineStatus(initialStatus)
        private set
    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun createActive(
            title: String,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            description: String= "",
        ): Election {
            return Election(
                title = title,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                description = description,
                initialStatus = ElectionStatus.ACTIVE
            )
        }

        fun createInActive(
            title: String,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            description: String= "",
        ): Election {
            return Election(
                title = title,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                description = description,
                initialStatus = ElectionStatus.INACTIVE
            )
        }
    }

    private fun determineStatus(initialStatus: ElectionStatus): ElectionStatus {
        return when {
            initialStatus == ElectionStatus.DELETED -> ElectionStatus.DELETED
            LocalDateTime.now().isBefore(endDateTime) -> ElectionStatus.ACTIVE
            LocalDateTime.now().isAfter(endDateTime) -> ElectionStatus.EXPIRED
            else -> initialStatus
        }
    }

    fun delete() {
        status = ElectionStatus.DELETED
        deletedAt = LocalDateTime.now()
    }
}