package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.repository.AccountRepository
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class FakeAccountRepository: AccountRepository {
    val map = mutableMapOf<Long, Account>()
    override fun save(account: Account): Account {
        val id = generateId()

        // Kotlin 리플렉션으로 id 필드 설정 (JDBC 구현체들이 동일하게 리플렉션으로 ID를 바꾸므로 테스트코드도 동일하게 변경)
        val kClass = account::class
        val idProperty = kClass.memberProperties.firstOrNull { it.name == "id" }

        idProperty?.let {
            it.javaField?.isAccessible = true
            it.javaField?.set(account, id)
        }

        map[id] = account
        return account
    }

    override fun getById(id: Long): Account {
        return map[id] ?: throw Exception("Account not found")
    }

    override fun getByIds(ids: List<Long>): Map<Long, Account> {
        TODO("Not yet implemented")
    }

    override fun findByCi(ci: String): Account? {
        TODO("Not yet implemented")
    }

    override fun getByPhoneNumberAndPassword(phoneNumber: String, password: String): Account {
        val account = map.values.find { it.phoneNumber == phoneNumber && it.password == password }
            ?: throw Exception("Account not found")
        return account
    }

    override fun getByPhoneNumberAndName(phoneNumber: String, name: String): Account {
        TODO("Not yet implemented")
    }

    private fun generateId(): Long {
        return map.keys.lastOrNull()?.plus(1) ?: 1L
    }
}