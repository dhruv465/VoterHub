package com.example.voterhub.data.remote

import com.example.voterhub.domain.model.Gender
import com.example.voterhub.domain.model.GenderFilter
import com.example.voterhub.domain.model.Prabhag
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.domain.model.Voter
import com.example.voterhub.domain.model.VoterListPage
import com.example.voterhub.domain.model.VoterQuery
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.delay

@Singleton
class FakeVoterApi @Inject constructor() : VoterApi {

    private val random = Random(42)

    private val prabhags = listOf(
        Prabhag("p1", "Prabhag 1", "प्रभाग 1", 3750),
        Prabhag("p2", "Prabhag 2", "प्रभाग 2", 3750),
        Prabhag("p3", "Prabhag 3", "प्रभाग 3", 3750),
        Prabhag("p4", "Prabhag 4", "प्रभाग 4", 3750),
        Prabhag("p5", "Prabhag 5", "प्रभाग 5", 3750),
        Prabhag("p6", "Prabhag 6", "प्रभाग 6", 3750),
        Prabhag("p7", "Prabhag 7", "प्रभाग 7", 3750),
        Prabhag("p8", "Prabhag 8", "प्रभाग 8", 3750),
        Prabhag("p9", "Prabhag 9", "प्रभाग 9", 3750),
        Prabhag("p10", "Prabhag 10", "प्रभाग 10", 3750),
        Prabhag("p11", "Prabhag 11", "प्रभाग 11", 3750),
        Prabhag("p12", "Prabhag 12", "प्रभाग 12", 3750)
    )

    private val sections = listOf(
        Section(
            id = "north",
            name = "North District",
            displayName = "उत्तर जिला",
            totalVoters = 45000,
            demographics = SectionDemographics(malePercentage = 48, femalePercentage = 50, avgAge = 37),
            prabhags = prabhags
        ),
        Section(
            id = "central",
            name = "Central District",
            displayName = "केंद्रीय जिला",
            totalVoters = 52000,
            demographics = SectionDemographics(malePercentage = 49, femalePercentage = 49, avgAge = 35),
            prabhags = prabhags
        ),
        Section(
            id = "south",
            name = "South District",
            displayName = "दक्षिण जिला",
            totalVoters = 39000,
            demographics = SectionDemographics(malePercentage = 47, femalePercentage = 51, avgAge = 34),
            prabhags = prabhags
        )
    )

    private val votersBySection: Map<String, Map<String, List<Voter>>> = sections.associate { section ->
        section.id to section.prabhags.associate { prabhag ->
            prabhag.id to buildList {
                repeat(100) { index ->
                    val gender = if (index % 2 == 0) Gender.Male else Gender.Female
                    val age = 18 + random.nextInt(60)
                    add(
                        Voter(
                            id = "${section.id}_${prabhag.id}_${index}",
                            fullName = "${generateSurname()} ${generateGivenName()}",
                            age = age,
                            gender = gender,
                            voterId = "ID${section.id.take(2).uppercase()}${100000 + random.nextInt(800000)}",
                            area = "${section.name} Ward ${1 + random.nextInt(20)}",
                            houseNumber = "H-${random.nextInt(1, 250)}",
                            relativeName = listOf("S/O", "D/O", "W/O").random(random) + " " + generateGivenName(),
                            sectionId = section.id,
                            prabhagId = prabhag.id
                        )
                    )
                }
            }
        }
    }

    override suspend fun getSections(): List<Section> {
        delay(120)
        return sections
    }

    override suspend fun getVoters(query: VoterQuery): VoterListPage {
        delay(180)
        val sectionVoters = votersBySection[query.sectionId] ?: emptyMap()
        val allCandidates = if (query.prabhagId != null) {
            sectionVoters[query.prabhagId].orEmpty()
        } else {
            sectionVoters.values.flatten()
        }
        
        val candidates = allCandidates.filter { voter ->
            matchesQuery(voter, query.searchQuery) &&
                matchesAge(voter, query.ageMin, query.ageMax) &&
                matchesGender(voter, query.gender)
        }
        val fromIndex = (query.page - 1) * query.pageSize
        val toIndex = (fromIndex + query.pageSize).coerceAtMost(candidates.size)
        val pageItems = if (fromIndex <= candidates.lastIndex) {
            candidates.subList(fromIndex, toIndex)
        } else {
            emptyList()
        }
        val hasMore = toIndex < candidates.size
        return VoterListPage(
            voters = pageItems,
            totalCount = candidates.size,
            page = query.page,
            pageSize = query.pageSize,
            hasMore = hasMore
        )
    }

    private fun matchesQuery(voter: Voter, query: String): Boolean {
        if (query.isBlank()) return true
        val q = query.lowercase()
        return voter.fullName.lowercase().contains(q) || (voter.relativeName?.lowercase()?.contains(q) == true)
    }

    private fun matchesAge(voter: Voter, min: Int?, max: Int?): Boolean {
        val minPass = min?.let { voter.age >= it } ?: true
        val maxPass = max?.let { voter.age <= it } ?: true
        return minPass && maxPass
    }

    private fun matchesGender(voter: Voter, genderFilter: GenderFilter): Boolean {
        return when (genderFilter) {
            GenderFilter.All -> true
            is GenderFilter.Specific -> voter.gender == genderFilter.gender
        }
    }

    private fun generateSurname(): String =
        listOf("Sharma", "Verma", "Khan", "Patel", "Singh", "Das", "Iyer").random(random)

    private fun generateGivenName(): String =
        listOf("Amit", "Priya", "Rohan", "Sneha", "Arjun", "Neha", "Vikram", "Pooja").random(random)
}

