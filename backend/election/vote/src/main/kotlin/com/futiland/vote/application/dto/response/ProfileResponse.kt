package com.futiland.vote.application.dto.response

data class ProfileResponse(
    val id: Long,
    val phoneNumber: String,
    val name: String,
    val createdAt: String,
)