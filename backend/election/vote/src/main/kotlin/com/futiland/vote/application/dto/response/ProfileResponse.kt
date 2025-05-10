package com.futiland.vote.application.dto.response

data class ProfileResponse(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(
            id: Long,
            name: String,
            email: String,
            createdAt: String,
            updatedAt: String
        ) = ProfileResponse(id, name, email, createdAt, updatedAt)
    }
}