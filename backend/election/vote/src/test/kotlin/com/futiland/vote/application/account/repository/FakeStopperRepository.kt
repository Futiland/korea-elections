package com.futiland.vote.application.account.repository

import com.futiland.vote.domain.account.entity.ServiceTarget
import com.futiland.vote.domain.account.entity.Stopper
import com.futiland.vote.domain.account.entity.StopperStatus
import com.futiland.vote.domain.account.repository.StopperRepository

class FakeStopperRepository(
    private val stopperStatus: StopperStatus
) : StopperRepository {
    override fun getStopper(serviceTarget: ServiceTarget): Stopper {
        return Stopper(
            serviceTarget = ServiceTarget.SIGNUP,
            stopperStatus = stopperStatus,
        )
    }
}