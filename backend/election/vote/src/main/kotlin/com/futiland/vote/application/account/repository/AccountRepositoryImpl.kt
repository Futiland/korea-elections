package com.futiland.vote.application.account.repository

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.AccountStatus
import com.futiland.vote.domain.account.repository.AccountRepository
import org.springframework.data.domain.PageRequest
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

    override fun getByIds(ids: List<Long>): Map<Long, Account> {
        return repository.findAllById(ids).associateBy { it.id }
    }

    override fun findByCi(ci: String): Account? {
        return repository.findByCi(ci)
    }

    override fun getByPhoneNumberAndPassword(phoneNumber: String, password: String): Account {
        return repository.findByPhoneNumberAndPasswordAndStatus(phoneNumber, password, status = AccountStatus.ACTIVE)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "가입되지 않은 사용자이거나 비밀번호가 틀립니다."
            )
    }

    override fun findAllPaged(page: Int, size: Int): List<Account> {
        val pageable = PageRequest.of(page, size)
        return repository.findAll(pageable).content
    }
}

interface JpaAccountRepository : JpaRepository<Account, Long> {
    fun findByPhoneNumberAndPasswordAndStatus(phoneNumber: String, password: String, status: AccountStatus): Account?
    fun findByCi(ci: String): Account?
    fun findByPhoneNumberAndName(phoneNumber: String, name: String): Account?
}