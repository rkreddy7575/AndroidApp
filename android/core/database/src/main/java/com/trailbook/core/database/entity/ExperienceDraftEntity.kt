package com.trailbook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experience_drafts")
data class ExperienceDraftEntity(
    @PrimaryKey val id: String,
    val jsonPayload: String,
    val currentStep: Int,
    val updatedAt: Long
)
