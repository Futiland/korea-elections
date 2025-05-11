package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Election
import com.futiland.vote.domain.vote.repository.ElectionRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class ElectionRepositoryImpl(
    private val repository: JpaElectionRepository
) : ElectionRepository {
    override fun getById(id: Long): Election {
        return repository.findById(id).orElseThrow {
            IllegalArgumentException("선거를 찾을 수 없습니다.")
        }
    }

    override fun save(election: Election): Election {
        return repository.save(election)
    }

    override fun findAllByStatus(): List<Election> {
        // 페이지네이션적용해야함
        TODO("Not yet implemented")
    }
}

interface JpaElectionRepository : JpaRepository<Election, Long> {
    fun findAllByStatus(status: String): List<Election>
}