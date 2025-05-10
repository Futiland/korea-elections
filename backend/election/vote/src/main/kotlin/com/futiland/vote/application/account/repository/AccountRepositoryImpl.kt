package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.repository.AccountRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class AccountRepositoryImpl(
    private val repository: JpaAccountRepository
) : AccountRepository {
    override fun save(account: Account): Account {
        return repository.save(account)
    }

    override fun getById(id: Long): Account {
        return repository.findById(id)
            .orElseThrow { IllegalArgumentException("Account not found with id: $id") }
    }

    override fun getByPhoneNumberAndPassword(phoneNumber: String, password: String): Account {
        return repository.findByPhoneNumberAndPassword(phoneNumber, password)
            ?: throw IllegalArgumentException("Account not found with phone number: $phoneNumber")
    }

}
interface JpaAccountRepository : JpaRepository<Account, Long> {
    fun findByPhoneNumberAndPassword(phoneNumber: String, password: String): Account?
}