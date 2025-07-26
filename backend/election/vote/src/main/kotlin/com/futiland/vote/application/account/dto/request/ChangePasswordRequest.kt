package com.futiland.vote.application.account.dto.request

data class ChangePasswordRequest (
    val verificationId: String,
    val password: String
)
