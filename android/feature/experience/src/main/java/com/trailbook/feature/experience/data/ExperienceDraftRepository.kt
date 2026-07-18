package com.trailbook.feature.experience.data

import com.google.gson.Gson
import com.trailbook.core.database.dao.ExperienceDraftDao
import com.trailbook.core.database.entity.ExperienceDraftEntity
import com.trailbook.feature.experience.ui.WizardState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperienceDraftRepository @Inject constructor(
    private val draftDao: ExperienceDraftDao,
    private val gson: Gson
) {
    suspend fun loadDraft(draftId: String): WizardState? {
        val entity = draftDao.getDraft(draftId) ?: return null
        return runCatching { gson.fromJson(entity.jsonPayload, WizardState::class.java) }
            .getOrNull()
            ?.copy(isSaving = false, error = null, isUploading = false)
    }

    suspend fun saveDraft(draftId: String, state: WizardState) {
        val payload = state.copy(isSaving = false, error = null, isUploading = false)
        draftDao.saveDraft(
            ExperienceDraftEntity(
                id = draftId,
                jsonPayload = gson.toJson(payload),
                currentStep = payload.currentStep,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearDraft(draftId: String) {
        draftDao.deleteDraft(draftId)
    }

    companion object {
        const val ACTIVE_DRAFT_ID = "active_wizard_draft"
    }
}
