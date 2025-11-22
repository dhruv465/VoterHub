package com.example.voterhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voterhub.data.local.entity.VoterEntity

@Dao
interface VoterDao {

    @Query(
        """
        SELECT * FROM voters
        WHERE sectionId = :sectionId
          AND (:prabhagId IS NULL OR prabhagId = :prabhagId)
          AND (:query IS NULL OR :query = '' OR LOWER(fullName) LIKE '%' || LOWER(:query) || '%' OR LOWER(relativeName) LIKE '%' || LOWER(:query) || '%' OR LOWER(voterId) LIKE '%' || LOWER(:query) || '%')
          AND (:ageMin IS NULL OR age >= :ageMin)
          AND (:ageMax IS NULL OR age <= :ageMax)
          AND (:gender IS NULL OR gender = :gender)
        ORDER BY fullName
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun queryVoters(
        sectionId: String,
        prabhagId: String?,
        query: String?,
        ageMin: Int?,
        ageMax: Int?,
        gender: String?,
        limit: Int,
        offset: Int
    ): List<VoterEntity>

    @Query(
        """
        SELECT COUNT(*) FROM voters
        WHERE sectionId = :sectionId
          AND (:prabhagId IS NULL OR prabhagId = :prabhagId)
          AND (:query IS NULL OR :query = '' OR LOWER(fullName) LIKE '%' || LOWER(:query) || '%' OR LOWER(relativeName) LIKE '%' || LOWER(:query) || '%')
          AND (:ageMin IS NULL OR age >= :ageMin)
          AND (:ageMax IS NULL OR age <= :ageMax)
          AND (:gender IS NULL OR gender = :gender)
        """
    )
    suspend fun countVoters(
        sectionId: String,
        prabhagId: String?,
        query: String?,
        ageMin: Int?,
        ageMax: Int?,
        gender: String?
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVoters(voters: List<VoterEntity>)

    @Query("DELETE FROM voters WHERE sectionId = :sectionId")
    suspend fun clearSection(sectionId: String)

    @Query("SELECT * FROM voters WHERE id = :id LIMIT 1")
    suspend fun getVoter(id: String): VoterEntity?
}

