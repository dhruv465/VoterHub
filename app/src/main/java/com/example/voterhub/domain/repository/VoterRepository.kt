package com.example.voterhub.domain.repository

import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.Voter
import com.example.voterhub.domain.model.VoterListPage
import com.example.voterhub.domain.model.VoterQuery
import kotlinx.coroutines.flow.Flow

interface VoterRepository {
    fun observeSections(): Flow<List<Section>>
    suspend fun refreshSections(forceNetwork: Boolean = false)
    suspend fun fetchVoters(query: VoterQuery): VoterListPage
    suspend fun getCachedVoter(id: String): Voter?
    suspend fun saveVotersToCache(voters: List<Voter>)
}

