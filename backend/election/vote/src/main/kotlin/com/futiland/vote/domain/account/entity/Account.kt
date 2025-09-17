package com.futiland.vote.domain.account.entity

import com.futiland.vote.application.common.EncryptConverter
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Convert(converter = EncryptConverter::class)
    val phoneNumber: String,
    @Convert(converter = EncryptConverter::class)
    val name: String,
    status: AccountStatus,
    password: String,
    @Enumerated(EnumType.STRING)
    val gender: Gender,
    val birthDate: LocalDate, // TODO 생년월일은 나중에 넣을 예정
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val ci: String,    // TODO 현재는 본인인증 못해서 못넣는데 곧 넣을 예정
) {

    @Convert(converter = EncryptConverter::class)
    var password: String = password
        private set

    @Enumerated(EnumType.STRING)
    var status: AccountStatus = status
        private set

    var updatedAt: LocalDateTime? = null

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

        fun migrateData(
            account:Account,
        ): Account {
            val account = Account(
                id = account.id,
                name = account.name,
                phoneNumber = account.phoneNumber,
                status = account.status,
                password = account.password,
                gender = account.gender,
                birthDate = account.birthDate,
                ci = account.ci
            )
            account.updatedAt = LocalDateTime.now()
            return account
        }
    }

    fun getAge(): Int {
        return LocalDate.now().year - birthDate.year - if (LocalDate.now().dayOfYear < birthDate.dayOfYear) 1 else 0
    }

    fun changePassword(newPassword: String): Account {
        this.password = newPassword
        this.updatedAt = LocalDateTime.now()
        return this
    }

    fun delete(): Account {
        this.deletedAt = LocalDateTime.now()
        this.status = AccountStatus.INACTIVE
        this.updatedAt = LocalDateTime.now()
        return this
    }
}