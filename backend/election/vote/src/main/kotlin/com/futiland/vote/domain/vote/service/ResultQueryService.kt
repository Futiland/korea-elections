package com.futiland.vote.domain.vote.service

import com.futiland.vote.application.vote.dto.response.CandidateQueryResponse
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.vote.dto.result.AgeGroup
import com.futiland.vote.domain.vote.dto.result.AgeGroupResultResponse
import com.futiland.vote.domain.vote.dto.result.CandidateResultDto
import com.futiland.vote.domain.vote.dto.result.GenderGroupResultResponse
import com.futiland.vote.domain.vote.repository.VoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class ResultQueryService(
    private val voteRepository: VoteRepository,
    private val accountRepository: AccountRepository,
    private val candidateRepository: com.futiland.vote.domain.vote.repository.CandidateRepository
) : ResultQueryUseCase {

    override fun getResultByAge(electionId: Long): AgeGroupResultResponse {
        val candidateInfoMap = getCandidateInfoMap(electionId)
        val ageGroupVotesMap = runBlocking {
            aggregateVotesByAgeGroup(electionId)
        }

        val results = ageGroupVotesMap.map { (ageGroup, candidateVotes) ->
            AgeGroupResultResponse.AgeGroupResult(
                ageGroup = ageGroup,
                candidateResults = candidateInfoMap.values.map { info ->
                    CandidateResultDto(
                        id = info.id,
                        number = info.number,
                        name = info.name,
                        party = info.party,
                        partyColor = info.partyColor,
                        partyStatus = info.partyStatus,
                        description = info.description,
                        voteCount = candidateVotes[info.id]?.toLong() ?: 0L
                    )
                },
                totalCount = candidateVotes.values.sum().toLong())
        }.sortedBy { it.ageGroup.age }
        val latestVoteTime = voteRepository.findLatestTimeByElectionId(electionId)
        return AgeGroupResultResponse(
            electionId = electionId,
            results = results,
            updatedAt = latestVoteTime,
        )
    }

    override fun getResultByGender(electionId: Long): GenderGroupResultResponse {
        val candidateInfoMap = getCandidateInfoMap(electionId)
        val ageGroupVotesMap = runBlocking {
            aggregateVotesByGenderGroup(electionId)
        }

        val results = ageGroupVotesMap.map { (genderGroup, candidateVotes) ->
            GenderGroupResultResponse.GenderGroupResult(
                genderGroup = genderGroup,
                candidateResults = candidateInfoMap.values.map  { info->
                    CandidateResultDto(
                        id = info.id,
                        number = info.number,
                        name = info.name,
                        party = info.party,
                        partyColor = info.partyColor,
                        partyStatus = info.partyStatus,
                        description = info.description,
                        voteCount = candidateVotes[info.id]?.toLong() ?: 0L
                    )
                }
            )
        }
        val latestVoteTime = voteRepository.findLatestTimeByElectionId(electionId)
        return GenderGroupResultResponse(
            electionId = electionId,
            results = results,
            updatedAt = latestVoteTime,
        )
    }

    suspend fun aggregateVotesByAgeGroup(electionId: Long): Map<AgeGroup, Map<Long, Int>> {
        return withContext(Dispatchers.IO) {
            val limit = 1000
            var lastVoteId: Long? = null
            val ageGroupVotesMap = mutableMapOf<AgeGroup, MutableMap<Long, Int>>()

            while (true) {
                val votes = voteRepository.findByElectionId(electionId, lastVoteId, limit)
                if (votes.isEmpty()) break

                val accountIds = votes.map { it.accountId }
                val accounts = accountRepository.getByIds(accountIds)

                for (vote in votes) {
                    val account = accounts[vote.accountId] ?: continue
                    val ageGroup = AgeGroup.fromAge(account.getAge())
                    val candidateVotes = ageGroupVotesMap.getOrPut(ageGroup) { mutableMapOf() }
                    candidateVotes[vote.selectedCandidateId] =
                        candidateVotes.getOrDefault(vote.selectedCandidateId, 0) + 1
                }

                lastVoteId = votes.last().id
                if (votes.size < limit) break
            }
            ageGroupVotesMap
        }
    }

    suspend fun aggregateVotesByGenderGroup(electionId: Long): Map<Gender, Map<Long, Int>> {
        return withContext(Dispatchers.IO) {
            val limit = 1000
            var lastVoteId: Long? = null
            val ageGroupVotesMap = mutableMapOf<Gender, MutableMap<Long, Int>>()

            while (true) {
                val votes = voteRepository.findByElectionId(electionId, lastVoteId, limit)
                if (votes.isEmpty()) break

                val accountIds = votes.map { it.accountId }
                val accounts = accountRepository.getByIds(accountIds)

                for (vote in votes) {
                    val account = accounts[vote.accountId] ?: continue
                    val genderGroup = account.gender
                    val candidateVotes = ageGroupVotesMap.getOrPut(genderGroup) { mutableMapOf() }
                    candidateVotes[vote.selectedCandidateId] =
                        candidateVotes.getOrDefault(vote.selectedCandidateId, 0) + 1
                }

                lastVoteId = votes.last().id
                if (votes.size < limit) break
            }
            ageGroupVotesMap
        }
    }

    private fun getCandidateInfoMap(electionId: Long): Map<Long, CandidateQueryResponse> {
        val candidates = candidateRepository.findByElectionId(electionId)
        return candidates.associate {
            it.id to CandidateQueryResponse(
                electionId = it.electionId,
                id = it.id,
                name = it.name,
                number = it.number,
                party = it.party.name,
                partyColor = it.party.color,
                partyStatus = it.party.status,
                description = it.description,
                createdAt = it.createdAt,
                deletedAt = it.deletedAt
            )
        }
    }
}