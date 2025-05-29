package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.application.account.dto.response.StopperResponse
import com.futiland.vote.domain.account.entity.ServiceTarget

interface AccountQueryUseCase {
    fun getProfileById(id: Long) : ProfileResponse
    fun isAlreadySignedUp(ci: String): Boolean
    fun getSignupStopper(): StopperResponse
}