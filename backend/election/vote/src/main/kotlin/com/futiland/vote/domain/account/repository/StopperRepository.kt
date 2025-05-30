package com.futiland.vote.domain.account.repository

import com.futiland.vote.domain.account.entity.ServiceTarget
import com.futiland.vote.domain.account.entity.Stopper

interface StopperRepository {
    fun getStopper(serviceTarget: ServiceTarget): Stopper
}