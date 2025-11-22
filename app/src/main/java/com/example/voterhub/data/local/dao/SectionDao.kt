package com.example.voterhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.voterhub.data.local.entity.PrabhagEntity
import com.example.voterhub.data.local.entity.SectionEntity
import com.example.voterhub.data.local.entity.SectionWithPrabhags
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    @Transaction
    @Query("SELECT * FROM sections ORDER BY name")
    fun observeSections(): Flow<List<SectionWithPrabhags>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSections(sections: List<SectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrabhags(prabhags: List<PrabhagEntity>)

    @Query("DELETE FROM prabhags WHERE sectionId = :sectionId")
    suspend fun clearPrabhags(sectionId: String)

    @Query("SELECT COUNT(*) FROM sections")
    suspend fun count(): Int
}

