package com.futiland.vote.application.account.dto.response

import com.futiland.vote.application.aop.masking.Masked
import com.futiland.vote.application.aop.masking.MaskingType

data class ProfileResponse(
    val id: Long,
    val phoneNumber: String,
    @Masked(type = MaskingType.NAME)
    val name: String,
    val createdAt: String,
)