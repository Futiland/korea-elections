package com.futiland.vote.domain.vote.entity

import jakarta.persistence.*

@Entity
class Party(
    val name: String,
    val color: String,
    status: PartyStatus,
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @Enumerated(EnumType.STRING)
    var status: PartyStatus = status
        private set
    var deletedAt: java.time.LocalDateTime? = null
        private set

    companion object {
        fun create(name: String, color: String): Party {
            return Party(
                name = name,
                status = PartyStatus.ACTIVE,
                color = color,
            )
        }
    }
}