package com.example.voterhub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.voterhub.data.local.dao.SectionDao
import com.example.voterhub.data.local.dao.VoterDao
import com.example.voterhub.data.local.entity.SectionEntity
import com.example.voterhub.data.local.entity.VoterEntity

@Database(
    entities = [
        SectionEntity::class,
        VoterEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sectionDao(): SectionDao
    abstract fun voterDao(): VoterDao
}

