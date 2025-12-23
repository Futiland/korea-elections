package com.futiland.vote.domain.vote.service

import com.futiland.vote.domain.vote.dto.result.AgeGroupResultResponse
import com.futiland.vote.domain.vote.dto.result.GenderGroupResultResponse

interface ResultQueryUseCase {
    fun getResultByAge(electionId: Long): AgeGroupResultResponse
    fun getResultByGender(electionId: Long): GenderGroupResultResponse
}