package com.trailbook.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trailbook.core.database.entity.ExperienceDraftEntity

@Dao
interface ExperienceDraftDao {
    @Query("SELECT * FROM experience_drafts WHERE id = :id")
    suspend fun getDraft(id: String): ExperienceDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: ExperienceDraftEntity)

    @Query("DELETE FROM experience_drafts WHERE id = :id")
    suspend fun deleteDraft(id: String)
}
