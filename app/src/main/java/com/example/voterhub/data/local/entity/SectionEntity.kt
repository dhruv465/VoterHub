package com.example.voterhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class SectionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val displayName: String,
    val totalVoters: Int,
    val malePercentage: Int,
    val femalePercentage: Int,
    val avgAge: Int
)

