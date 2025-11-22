package com.example.voterhub.data.mapper

import com.example.voterhub.data.local.entity.PrabhagEntity
import com.example.voterhub.data.local.entity.SectionEntity
import com.example.voterhub.data.local.entity.SectionWithPrabhags
import com.example.voterhub.data.local.entity.VoterEntity
import com.example.voterhub.domain.model.Gender
import com.example.voterhub.domain.model.Prabhag
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.domain.model.Voter

fun SectionWithPrabhags.toDomain(): Section = Section(
    id = section.id,
    name = section.name,
    displayName = section.displayName,
    totalVoters = section.totalVoters,
    demographics = SectionDemographics(
        malePercentage = section.malePercentage,
        femalePercentage = section.femalePercentage,
        avgAge = section.avgAge
    ),
    prabhags = prabhags.map { it.toDomain() }
)

fun PrabhagEntity.toDomain(): Prabhag = Prabhag(
    id = id,
    name = name,
    displayName = displayName,
    totalVoters = totalVoters
)

fun Section.toEntity(): SectionEntity = SectionEntity(
    id = id,
    name = name,
    displayName = displayName,
    totalVoters = totalVoters,
    malePercentage = demographics.malePercentage,
    femalePercentage = demographics.femalePercentage,
    avgAge = demographics.avgAge
)

fun Prabhag.toEntity(sectionId: String): PrabhagEntity = PrabhagEntity(
    id = id,
    sectionId = sectionId,
    name = name,
    displayName = displayName,
    totalVoters = totalVoters
)

fun VoterEntity.toDomain(): Voter = Voter(
    id = id,
    fullName = fullName,
    age = age,
    gender = Gender.valueOf(gender),
    voterId = voterId,
    area = area,
    houseNumber = houseNumber,
    relativeName = relativeName,
    sectionId = sectionId,
    prabhagId = prabhagId
)

fun Voter.toEntity(): VoterEntity = VoterEntity(
    id = id,
    fullName = fullName,
    age = age,
    gender = gender.name,
    voterId = voterId,
    area = area,
    houseNumber = houseNumber,
    relativeName = relativeName,
    sectionId = sectionId,
    prabhagId = prabhagId
)

