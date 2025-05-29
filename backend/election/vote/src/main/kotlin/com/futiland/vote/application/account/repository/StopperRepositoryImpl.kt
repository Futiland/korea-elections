package com.futiland.vote.application.account.repository

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.entity.ServiceTarget
import com.futiland.vote.domain.account.entity.Stopper
import com.futiland.vote.domain.account.repository.StopperRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class StopperRepositoryImpl(
    private val repository: JpaStopperRepository
) : StopperRepository {
    override fun getStopper(serviceTarget: ServiceTarget): Stopper {
        return repository.findByServiceTarget(serviceTarget)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "No stopper found for service target: ${serviceTarget.description}"
            )
    }
}

interface JpaStopperRepository : JpaRepository<Stopper, Long> {
    fun findByServiceTarget(serviceTarget: ServiceTarget): Stopper?
}