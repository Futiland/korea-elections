package com.futiland.vote.application.vote.repository

import com.futiland.vote.domain.vote.entity.Election
import com.futiland.vote.domain.vote.entity.ElectionStatus
import com.futiland.vote.domain.vote.repository.ElectionRepository
import com.futiland.vote.util.SliceContent
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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

    override fun findAll(size: Int, nextCursor: String?): SliceContent<Election> {
        val pageable = PageRequest.ofSize(size)
        val content = if (nextCursor == null) {
            repository.getElectionsFromLatest(status = listOf(ElectionStatus.ACTIVE), pageable = pageable)
        } else {
            repository.getElectionsFromLastId(
                electionId = nextCursor.toLong(),
                status = listOf(ElectionStatus.ACTIVE),
                pageable = pageable
            )
        }
        val id: String? = if (content.isEmpty() || content.size < size)
            null
        else
            content[content.size - 1].id.toString()
        return SliceContent(content, id)
    }
}

interface JpaElectionRepository : JpaRepository<Election, Long> {
    fun findAllByStatus(status: String): List<Election>

    @Query(
        """
        SELECT e FROM Election e
        WHERE e.status in :status
        ORDER BY e.id DESC
        """
    )
    fun getElectionsFromLatest(status: List<ElectionStatus>, pageable: PageRequest): List<Election>

    @Query(
        """
        SELECT e FROM Election e
        WHERE e.id < :electionId and e.status in :status
        ORDER BY e.id DESC
        """
    )
    fun getElectionsFromLastId(
        electionId: Long,
        status: List<ElectionStatus>,
        pageable: PageRequest
    ): List<Election>
}