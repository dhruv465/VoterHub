package com.example.voterhub.data.mapper

import com.example.voterhub.data.local.entity.SectionEntity
import com.example.voterhub.data.local.entity.VoterEntity
import com.example.voterhub.domain.model.Gender
import com.example.voterhub.domain.model.Section
import com.example.voterhub.domain.model.SectionDemographics
import com.example.voterhub.domain.model.Voter

fun SectionEntity.toDomain(): Section = Section(
    id = id,
    name = name,
    displayName = displayName,
    totalVoters = totalVoters,
    demographics = SectionDemographics(
        malePercentage = malePercentage,
        femalePercentage = femalePercentage,
        avgAge = avgAge
    )
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

