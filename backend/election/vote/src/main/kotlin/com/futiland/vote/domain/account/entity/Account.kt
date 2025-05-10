package com.futiland.vote.domain.account.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Account(
    val phoneNumber: String,
    val name: String,
    status: AccountStatus,
    password: String,
    @Enumerated(EnumType.STRING)
    val gender: Gender,
    val birthDate: LocalDate, // TODO 생년월일은 나중에 넣을 예정
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val ci: String,    // TODO 현재는 본인인증 못해서 못넣는데 곧 넣을 예정
) {
    @Id
    val id: Long = 0
    var password: String = password
        private set

    @Enumerated(EnumType.STRING)
    var status: AccountStatus = status
        private set

    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun create(
            name: String,
            phoneNumber: String,
            password: String,
            gender: Gender,
            birthDate: LocalDate,
            ci: String
        ): Account {
            return Account(
                name = name,
                phoneNumber = phoneNumber,
                status = AccountStatus.ACTIVE,
                password = password,
                gender = gender,
                birthDate = birthDate,
                ci = ci
            )
        }
    }
}