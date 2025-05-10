package com.futiland.vote.domain.account.entity

import java.time.LocalDate
import java.time.LocalDateTime

class Account(
    val phoneNumber: String,
    status: AccountStatus,
    password: String,
    val gender: Gender,
    val birthDate: LocalDate, // TODO 생년월일은 나중에 넣을 예정
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val ci: String,    // TODO 현재는 본인인증 못해서 못넣는데 곧 넣을 예정
) {
    val id: Long = 0
    var password: String = password
        private set

    var status: AccountStatus = status
        private set

    companion object {
        fun create(
            phoneNumber: String,
            password: String,
            gender: Gender,
            birthDate: LocalDate,
            ci: String
        ): Account {
            return Account(
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