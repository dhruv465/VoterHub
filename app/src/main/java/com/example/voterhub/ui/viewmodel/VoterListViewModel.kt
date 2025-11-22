package com.example.voterhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voterhub.R
import com.example.voterhub.domain.model.AgeRange
import com.example.voterhub.domain.model.FilterState
import com.example.voterhub.domain.model.GenderFilter
import com.example.voterhub.domain.model.VoterQuery
import com.example.voterhub.domain.repository.SearchHistoryRepository
import com.example.voterhub.domain.repository.VoterRepository
import com.example.voterhub.sync.SyncStatusTracker
import com.example.voterhub.ui.state.VoterListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VoterListViewModel @Inject constructor(
    private val voterRepository: VoterRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 50
        val AgeRanges = listOf(
            AgeRange("18-25", 18, 25),
            AgeRange("26-35", 26, 35),
            AgeRange("36-45", 36, 45),
            AgeRange("46-60", 46, 60),
            AgeRange("60+", 60, null)
        )
    }

    private val sectionId: String = checkNotNull(savedStateHandle["sectionId"])
    private val prabhagId: String? = savedStateHandle["prabhagId"]

    private val _uiState = MutableStateFlow(VoterListUiState(selectedSectionId = sectionId))
    val uiState: StateFlow<VoterListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isPageLoading = false
    private var searchDebounceJob: Job? = null

    init {
        observeSearchHistory()
        loadFirstPage()
    }

    private fun observeSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.recentSearches.collectLatest { searches ->
                _uiState.update { it.copy(recentSearches = searches) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(filterState = recompute(state.filterState.copy(searchQuery = query)))
        }
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(300)
            if (query.isNotBlank()) {
                searchHistoryRepository.saveQuery(query)
            }
            loadFirstPage()
        }
    }

    fun onRecentSearchSelected(query: String) {
        onSearchQueryChange(query)
    }

    fun onAgeRangeSelected(range: AgeRange?) {
        updateFilter { it.copy(ageRange = range) }
        loadFirstPage()
    }

    fun onGenderFilterSelected(filter: GenderFilter) {
        updateFilter { it.copy(genderFilter = filter) }
        loadFirstPage()
    }

    fun onCustomAgeRangeSelected(min: String, max: String) {
        val minAge = min.toIntOrNull()
        val maxAge = max.toIntOrNull()
        when {
            minAge == null || maxAge == null -> _uiState.update { it.copy(customAgeRangeError = R.string.enter_valid_age) }
            minAge > maxAge -> _uiState.update { it.copy(customAgeRangeError = R.string.min_less_than_max) }
            minAge < 18 -> _uiState.update { it.copy(customAgeRangeError = R.string.min_age_18) }
            else -> {
                _uiState.update { it.copy(customAgeRangeError = null) }
                onAgeRangeSelected(AgeRange(label = "Custom $minAge-$maxAge", min = minAge, max = maxAge))
            }
        }
    }

    fun clearCustomAgeRangeError() {
        _uiState.update { it.copy(customAgeRangeError = null) }
    }

    fun clearFilters() {
        updateFilter { FilterState(searchQuery = "") }
        loadFirstPage()
    }

    fun toggleFilterSheet(show: Boolean) {
        _uiState.update { it.copy(isFilterSheetVisible = show) }
    }

    fun loadNextPage() {
        if (_uiState.value.hasMore) {
            loadPage(reset = false)
        }
    }

    fun onPullToRefresh() {
        loadFirstPage()
    }

    private fun loadFirstPage() {
        currentPage = 1
        loadPage(reset = true)
    }

    private fun loadPage(reset: Boolean) {
        if (isPageLoading) return
        viewModelScope.launch {
            isPageLoading = true
            if (reset) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            val filter = _uiState.value.filterState
            val query = VoterQuery(
                sectionId = sectionId,
                prabhagId = prabhagId,
                page = currentPage,
                pageSize = PAGE_SIZE,
                searchQuery = filter.searchQuery,
                ageMin = filter.ageRange?.min,
                ageMax = filter.ageRange?.max,
                gender = filter.genderFilter
            )
            runCatching {
                voterRepository.fetchVoters(query)
            }.onSuccess { page ->
                _uiState.update {
                    it.copy(
                        voters = if (reset) page.voters else it.voters + page.voters,
                        totalCount = page.totalCount,
                        hasMore = page.hasMore,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                if (page.hasMore) {
                    currentPage += 1
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load voters"
                    )
                }
            }
            isPageLoading = false
        }
    }

    private fun updateFilter(transform: (FilterState) -> FilterState) {
        _uiState.update { state ->
            state.copy(filterState = recompute(transform(state.filterState)))
        }
    }

    private fun recompute(filterState: FilterState): FilterState {
        val activeFilters = buildList {
            if (filterState.searchQuery.isNotBlank()) add("search")
            if (filterState.ageRange != null) add("age")
            if (filterState.genderFilter is GenderFilter.Specific) add("gender")
        }.size
        return filterState.copy(activeFilterCount = activeFilters)
    }
}

