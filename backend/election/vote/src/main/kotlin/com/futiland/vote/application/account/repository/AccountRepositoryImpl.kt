package com.futiland.vote.application.account.repository

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
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

    override fun findByCi(ci: String): Account? {
        return repository.findByCi(ci)
    }

    override fun getByPhoneNumberAndPassword(phoneNumber: String, password: String): Account {
        return repository.findByPhoneNumberAndPassword(phoneNumber, password)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "가입되지 않은 사용자이거나 비밀번호가 틀립니다."
            )
    }

}
interface JpaAccountRepository : JpaRepository<Account, Long> {
    fun findByPhoneNumberAndPassword(phoneNumber: String, password: String): Account?
    fun findByCi(ci: String): Account?
}