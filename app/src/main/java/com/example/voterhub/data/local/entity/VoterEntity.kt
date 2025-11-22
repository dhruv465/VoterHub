package com.example.voterhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voters")
data class VoterEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val voterId: String?,
    val area: String,
    val houseNumber: String?,
    val relativeName: String?,
    val sectionId: String,
    val prabhagId: String? = null
)

