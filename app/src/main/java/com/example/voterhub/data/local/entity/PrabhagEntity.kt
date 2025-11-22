package com.example.voterhub.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "prabhags",
    primaryKeys = ["id", "sectionId"],
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PrabhagEntity(
    val id: String,
    val sectionId: String,
    val name: String,
    val displayName: String,
    val totalVoters: Int
)
