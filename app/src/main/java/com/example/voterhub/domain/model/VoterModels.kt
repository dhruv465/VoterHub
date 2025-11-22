package com.example.voterhub.domain.model

data class Section(
    val id: String,
    val name: String,
    val displayName: String,
    val totalVoters: Int,
    val demographics: SectionDemographics = SectionDemographics(),
    val prabhags: List<Prabhag> = emptyList()
)

data class Prabhag(
    val id: String,
    val name: String,
    val displayName: String,
    val totalVoters: Int
)

data class SectionDemographics(
    val malePercentage: Int = 0,
    val femalePercentage: Int = 0,
    val avgAge: Int = 0
)

data class Voter(
    val id: String,
    val fullName: String,
    val age: Int,
    val gender: Gender,
    val voterId: String?,
    val area: String,
    val houseNumber: String?,
    val relativeName: String?,
    val sectionId: String,
    val prabhagId: String? = null
)

data class VoterListPage(
    val voters: List<Voter>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean
)

data class VoterQuery(
    val sectionId: String,
    val prabhagId: String? = null,
    val page: Int,
    val pageSize: Int,
    val searchQuery: String = "",
    val ageMin: Int? = null,
    val ageMax: Int? = null,
    val gender: GenderFilter = GenderFilter.All
)

data class AgeRange(
    val label: String,
    val min: Int?,
    val max: Int?
)

enum class Gender {
    Male,
    Female,
    Other
}

sealed interface GenderFilter {
    object All : GenderFilter
    data class Specific(val gender: Gender) : GenderFilter
}

data class FilterState(
    val ageRange: AgeRange? = null,
    val genderFilter: GenderFilter = GenderFilter.All,
    val searchQuery: String = "",
    val activeFilterCount: Int = 0
)

