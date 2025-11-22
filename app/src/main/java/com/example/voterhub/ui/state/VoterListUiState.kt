package com.example.voterhub.ui.state

import com.example.voterhub.domain.model.FilterState
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.domain.model.Voter

data class VoterListUiState(
    val sections: List<Section> = emptyList(),
    val selectedSectionId: String? = null,
    val voters: List<Voter> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val hasMore: Boolean = false,
    val errorMessage: String? = null,
    val filterState: FilterState = FilterState(),
    val recentSearches: List<String> = emptyList(),
    val sectionDemographics: SectionDemographics? = null,
    val customAgeRangeError: Int? = null
)

