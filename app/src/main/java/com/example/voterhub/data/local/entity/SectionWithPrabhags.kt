package com.example.voterhub.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SectionWithPrabhags(
    @Embedded val section: SectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sectionId"
    )
    val prabhags: List<PrabhagEntity>
)
