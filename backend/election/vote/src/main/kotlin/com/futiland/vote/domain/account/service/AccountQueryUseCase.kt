package com.futiland.vote.domain.account.service

import com.futiland.vote.application.dto.response.ProfileResponse

interface AccountQueryUseCase {
    fun getProfileById(id: Long) : ProfileResponse
}