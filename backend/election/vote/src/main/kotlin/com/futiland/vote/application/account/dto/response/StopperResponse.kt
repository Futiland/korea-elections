package com.futiland.vote.application.account.dto.response

import com.futiland.vote.domain.account.entity.StopperStatus

data class StopperResponse(
    val status: StopperStatus,
    val message: String,
)
