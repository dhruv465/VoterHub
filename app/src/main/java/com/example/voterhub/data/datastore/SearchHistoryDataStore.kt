package com.example.voterhub.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.voterhub.domain.repository.SearchHistoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.searchHistoryDataStore by preferencesDataStore(name = "search_history")

@Singleton
class SearchHistoryDataStore @Inject constructor(
    @ApplicationContext context: Context
) : SearchHistoryRepository {

    companion object {
        private val KEY_RECENT = stringPreferencesKey("recent_searches")
        private val KEY_LAST_SECTION = stringPreferencesKey("last_section_id")
        private const val MAX_RECENT = 5
    }

    private val dataStore = context.searchHistoryDataStore

    override val recentSearches: Flow<List<String>> =
        dataStore.data.map { preferences ->
            decode(preferences[KEY_RECENT])
        }

    override suspend fun saveQuery(query: String) {
        if (query.isBlank()) return
        dataStore.edit { preferences ->
            val current = decode(preferences[KEY_RECENT])
            val updated = listOf(query.trim()) + current.filterNot { it.equals(query, true) }
            preferences[KEY_RECENT] = encode(updated.take(MAX_RECENT))
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs -> prefs.remove(KEY_RECENT) }
    }

    override val lastSectionId: Flow<String?> =
        dataStore.data.map { preferences -> preferences[KEY_LAST_SECTION] }

    override suspend fun saveLastSection(sectionId: String) {
        dataStore.edit { prefs -> prefs[KEY_LAST_SECTION] = sectionId }
    }

    private fun encode(list: List<String>): String = JSONArray(list).toString()

    private fun decode(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return runCatching {
            val jsonArray = JSONArray(value)
            val size = jsonArray.length()
            buildList(size) {
                repeat(size) { index ->
                    add(jsonArray.getString(index))
                }
            }
        }.getOrDefault(emptyList())
    }
}

