package com.futiland.vote.application.account.dto.response

data class AccountStatsResponse(
    val createdPollCount: Long,
    val participatedPublicPollCount: Long,
    val participatedSystemPollCount: Long
)
