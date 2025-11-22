package com.example.voterhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.domain.repository.SearchHistoryRepository
import com.example.voterhub.domain.repository.VoterRepository
import com.example.voterhub.sync.SyncStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val sections: List<Section> = emptyList(),
    val selectedSectionId: String? = null,
    val sectionDemographics: SectionDemographics? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val voterRepository: VoterRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val syncStatusTracker: SyncStatusTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeSections()
        refreshSections()
    }

    private fun observeSections() {
        viewModelScope.launch {
            voterRepository.observeSections()
                .combine(searchHistoryRepository.lastSectionId) { sections, lastSection ->
                    sections to lastSection
                }
                .collectLatest { (sections, lastSection) ->
                    val selected = lastSection ?: _uiState.value.selectedSectionId ?: sections.firstOrNull()?.id
                    _uiState.update {
                        it.copy(
                            sections = sections,
                            selectedSectionId = selected,
                            sectionDemographics = sections.firstOrNull { section -> section.id == selected }?.demographics,
                            isLoading = sections.isEmpty()
                        )
                    }
                    if (sections.isNotEmpty() && lastSection == null && selected != null) {
                        searchHistoryRepository.saveLastSection(selected)
                    }
                }
        }
    }

    fun refreshSections(forceNetwork: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            runCatching {
                voterRepository.refreshSections(forceNetwork)
                if (forceNetwork) {
                    syncStatusTracker.triggerImmediateSync()
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onSectionSelected(sectionId: String) {
        if (sectionId == _uiState.value.selectedSectionId) return
        viewModelScope.launch {
            searchHistoryRepository.saveLastSection(sectionId)
        }
        _uiState.update {
            it.copy(
                selectedSectionId = sectionId,
                sectionDemographics = it.sections.firstOrNull { section -> section.id == sectionId }?.demographics
            )
        }
    }
}
