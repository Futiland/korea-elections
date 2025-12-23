package com.futiland.vote.domain.account.repository

import com.futiland.vote.domain.account.entity.Account

interface AccountRepository {
    fun save(account: Account): Account
    fun getById(id: Long): Account
    fun getByIds(ids: List<Long>): Map<Long, Account>
    fun findByCi(ci: String): Account?
    fun getByPhoneNumberAndPassword(phoneNumber: String, password: String): Account
    fun findAllPaged(page: Int, size: Int): List<Account>}