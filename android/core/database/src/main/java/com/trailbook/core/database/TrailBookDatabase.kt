package com.trailbook.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trailbook.core.database.dao.ExperienceDraftDao
import com.trailbook.core.database.entity.ExperienceDraftEntity

@Database(
    entities = [ExperienceDraftEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TrailBookDatabase : RoomDatabase() {
    abstract fun experienceDraftDao(): ExperienceDraftDao
}
