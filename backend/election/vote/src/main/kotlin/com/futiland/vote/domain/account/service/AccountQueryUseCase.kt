package com.futiland.vote.domain.account.service

import com.futiland.vote.application.dto.response.ProfileResponse
import com.futiland.vote.domain.account.entity.Account

interface AccountQueryUseCase {
    fun getProfileById(id: Long) : ProfileResponse
}