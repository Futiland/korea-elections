package com.futiland.vote.domain.account.entity

import java.time.LocalDateTime

class Account(
    val phoneNumber: String,
    status: AccountStatus,
    password: String,
    val gender: Gender,
    val birthDate: LocalDateTime = LocalDateTime.now(), // TODO 생년월일은 나중에 넣을 예정
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val ci: String = "",    // TODO 현재는 본인인증 못해서 못넣는데 곧 넣을 예정
) {
    val id: Long = 0
    var password: String = password
        private set

}