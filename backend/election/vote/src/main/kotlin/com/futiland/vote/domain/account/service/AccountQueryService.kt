package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.application.account.dto.response.StopperResponse
import com.futiland.vote.domain.account.entity.ServiceTarget
import com.futiland.vote.domain.account.entity.Stopper
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.account.repository.StopperRepository
import org.springframework.stereotype.Service

@Service
class AccountQueryService(
    private val accountRepository: AccountRepository,
    private val stopperRepository: StopperRepository
) : AccountQueryUseCase {
    override fun getProfileById(id: Long): ProfileResponse {
        val account = accountRepository.getById(id = id)
        return ProfileResponse(
            id = account.id,
            phoneNumber = account.phoneNumber,
            name = account.name,
            createdAt = account.createdAt.toString()
        )
    }

    override fun isAlreadySignedUp(ci: String): Boolean {
        val account = accountRepository.findByCi(ci)
        return account != null
    }

    override fun getSignupStopper(): StopperResponse {
        val stopper: Stopper = stopperRepository.getStopper(serviceTarget = ServiceTarget.SIGNUP)
        return StopperResponse(
            status = stopper.status,
            message = stopper.message
        )
    }
}