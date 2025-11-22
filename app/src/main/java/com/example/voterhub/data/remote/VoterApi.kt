package com.example.voterhub.data.remote

import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.VoterListPage
import com.example.voterhub.domain.model.VoterQuery

interface VoterApi {
    suspend fun getSections(): List<Section>
    suspend fun getVoters(query: VoterQuery): VoterListPage
}

