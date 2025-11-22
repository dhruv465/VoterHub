package com.example.voterhub.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    val recentSearches: Flow<List<String>>
    suspend fun saveQuery(query: String)
    suspend fun clear()
    val lastSectionId: Flow<String?>
    suspend fun saveLastSection(sectionId: String)
}

