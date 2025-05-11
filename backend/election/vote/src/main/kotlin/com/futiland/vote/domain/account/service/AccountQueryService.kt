package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountQueryService(
    private val accountRepository: AccountRepository
):AccountQueryUseCase {
    override fun getProfileById(id: Long): ProfileResponse {
        val account = accountRepository.getById(id=id)
        return ProfileResponse(
            id = account.id,
            phoneNumber = account.phoneNumber,
            name = account.name,
            createdAt = account.createdAt.toString()
        )
    }
}