package com.example.voterhub.data.repository

import com.example.voterhub.data.local.dao.SectionDao
import com.example.voterhub.data.local.dao.VoterDao
import com.example.voterhub.data.mapper.toDomain
import com.example.voterhub.data.mapper.toEntity
import com.example.voterhub.data.remote.VoterApi
import com.example.voterhub.di.IoDispatcher
import com.example.voterhub.domain.model.GenderFilter
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.Voter
import com.example.voterhub.domain.model.VoterListPage
import com.example.voterhub.domain.model.VoterQuery
import com.example.voterhub.domain.repository.VoterRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class OfflineFirstVoterRepository @Inject constructor(
    private val sectionDao: SectionDao,
    private val voterDao: VoterDao,
    private val api: VoterApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VoterRepository {

    override fun observeSections(): Flow<List<Section>> =
        sectionDao.observeSections().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun refreshSections(forceNetwork: Boolean) = withContext(ioDispatcher) {
        val shouldFetch = forceNetwork || sectionDao.count() == 0
        if (!shouldFetch) return@withContext
        val sections = api.getSections()
        sectionDao.upsertSections(sections.map { it.toEntity() })
        val prabhags = sections.flatMap { section ->
            section.prabhags.map { it.toEntity(sectionId = section.id) }
        }
        sectionDao.upsertPrabhags(prabhags)
    }

    override suspend fun fetchVoters(query: VoterQuery): VoterListPage = withContext(ioDispatcher) {
        runCatching {
            val page = api.getVoters(query)
            voterDao.upsertVoters(page.voters.map { it.toEntity() })
            page
        }.getOrElse {
            val voters = voterDao.queryVoters(
                sectionId = query.sectionId,
                prabhagId = query.prabhagId,
                query = query.searchQuery,
                ageMin = query.ageMin,
                ageMax = query.ageMax,
                gender = query.gender.toQueryValue(),
                limit = query.pageSize,
                offset = (query.page - 1) * query.pageSize
            ).map { it.toDomain() }
            val total = voterDao.countVoters(
                sectionId = query.sectionId,
                prabhagId = query.prabhagId,
                query = query.searchQuery,
                ageMin = query.ageMin,
                ageMax = query.ageMax,
                gender = query.gender.toQueryValue()
            )
            VoterListPage(
                voters = voters,
                totalCount = total,
                page = query.page,
                pageSize = query.pageSize,
                hasMore = (query.page * query.pageSize) < total
            )
        }
    }

    override suspend fun getCachedVoter(id: String): Voter? = withContext(ioDispatcher) {
        voterDao.getVoter(id)?.toDomain()
    }

    override suspend fun saveVotersToCache(voters: List<Voter>) = withContext(ioDispatcher) {
        voterDao.upsertVoters(voters.map { it.toEntity() })
    }

    private fun GenderFilter.toQueryValue(): String? = when (this) {
        GenderFilter.All -> null
        is GenderFilter.Specific -> gender.name
    }
}

