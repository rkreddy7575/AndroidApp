package com.trailbook.feature.experience.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trailbook.core.common.Result
import com.trailbook.core.common.UiState
import com.trailbook.core.network.model.*
import com.trailbook.feature.experience.data.ExperienceDraftRepository
import com.trailbook.feature.experience.data.ExperienceDraftRepository.Companion.ACTIVE_DRAFT_ID
import com.trailbook.feature.experience.data.ExperienceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WizardState(
    val experienceId: String? = null,
    val currentStep: Int = 0,
    val title: String = "",
    val overview: String = "",
    val destination: String = "",
    val coverImageUrl: String = "",
    val timeline: List<TimelineEntryDto> = emptyList(),
    val budget: List<BudgetItemDto> = emptyList(),
    val accommodation: List<AccommodationDto> = emptyList(),
    val food: List<FoodSpotDto> = emptyList(),
    val transportation: List<TransportationDto> = emptyList(),
    val gallery: List<GalleryItemDto> = emptyList(),
    val tips: List<TipDto> = emptyList(),
    val packing: List<PackingItemDto> = emptyList(),
    val isSaving: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null
) {
    val totalSteps = 9
}

@HiltViewModel
class CreateExperienceViewModel @Inject constructor(
    private val repository: ExperienceRepository,
    private val draftRepository: ExperienceDraftRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val editId: String? = savedStateHandle["experienceId"]
    private val localDraftId = editId?.let { "edit_$it" } ?: ACTIVE_DRAFT_ID

    private val _state = MutableStateFlow(WizardState(experienceId = editId))
    val state: StateFlow<WizardState> = _state.asStateFlow()

    private val _publishState = MutableStateFlow<UiState<String>?>(null)
    val publishState: StateFlow<UiState<String>?> = _publishState.asStateFlow()

    init {
        if (editId != null) {
            loadForEdit(editId)
        } else {
            loadLocalDraft()
        }
    }

    private fun loadLocalDraft() {
        viewModelScope.launch {
            val draft = draftRepository.loadDraft(localDraftId)
            if (draft != null) {
                _state.value = draft
            }
        }
    }

    private fun loadForEdit(id: String) {
        viewModelScope.launch {
            val local = draftRepository.loadDraft(localDraftId)
            if (local != null) {
                _state.value = local
                return@launch
            }
            when (val result = repository.getById(id)) {
                is Result.Success -> {
                    val e = result.data
                    _state.value = WizardState(
                        experienceId = e.id,
                        title = e.title,
                        overview = e.overview ?: "",
                        destination = e.destination ?: "",
                        coverImageUrl = e.coverImageUrl ?: "",
                        timeline = e.timeline,
                        budget = e.budget,
                        accommodation = e.accommodation,
                        food = e.food,
                        transportation = e.transportation,
                        gallery = e.gallery,
                        tips = e.tips,
                        packing = e.packing
                    )
                    persistLocalDraft()
                }
                is Result.Error -> _state.value = _state.value.copy(error = result.message)
            }
        }
    }

    private fun updateState(transform: (WizardState) -> WizardState) {
        _state.value = transform(_state.value)
        persistLocalDraft()
    }

    private fun persistLocalDraft() {
        viewModelScope.launch {
            draftRepository.saveDraft(localDraftId, _state.value)
        }
    }

    fun updateTitle(v: String) = updateState { it.copy(title = v) }
    fun updateOverview(v: String) = updateState { it.copy(overview = v) }
    fun updateDestination(v: String) = updateState { it.copy(destination = v) }
    fun updateCoverUrl(v: String) = updateState { it.copy(coverImageUrl = v) }

    fun addTimelineEntry(day: Int, title: String, description: String) {
        val entry = TimelineEntryDto(null, day, title, description, _state.value.timeline.size)
        updateState { it.copy(timeline = it.timeline + entry) }
    }

    fun addBudget(category: String, amount: Double) {
        val item = BudgetItemDto(null, category, amount, "USD", null)
        updateState { it.copy(budget = it.budget + item) }
    }

    fun addAccommodation(name: String, location: String) {
        val item = AccommodationDto(null, name, location, null, null)
        updateState { it.copy(accommodation = it.accommodation + item) }
    }

    fun addFood(name: String, cuisine: String) {
        val item = FoodSpotDto(null, name, cuisine, null, null)
        updateState { it.copy(food = it.food + item) }
    }

    fun addGalleryUrl(url: String) {
        val item = GalleryItemDto(null, url, null, _state.value.gallery.size)
        updateState { it.copy(gallery = it.gallery + item) }
    }

    fun addTip(content: String) {
        updateState { it.copy(tips = it.tips + TipDto(null, content)) }
    }

    fun addPacking(item: String) {
        updateState { it.copy(packing = it.packing + PackingItemDto(null, item, false)) }
    }

    fun uploadImageFromUri(uri: Uri, forCover: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true, error = null)
            val bytes = readBytes(uri)
            if (bytes == null) {
                _state.value = _state.value.copy(isUploading = false, error = "Could not read image")
                return@launch
            }
            val fileName = "trailbook_${System.currentTimeMillis()}.jpg"
            when (val result = repository.uploadImage(bytes, fileName)) {
                is Result.Success -> {
                    if (forCover) {
                        updateState { it.copy(coverImageUrl = result.data, isUploading = false) }
                    } else {
                        val item = GalleryItemDto(null, result.data, null, _state.value.gallery.size)
                        updateState { it.copy(gallery = it.gallery + item, isUploading = false) }
                    }
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isUploading = false, error = result.message)
                }
            }
        }
    }

    private fun readBytes(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Exception) {
            null
        }
    }

    fun nextStep() {
        val s = _state.value
        if (s.currentStep < s.totalSteps - 1) {
            _state.value = s.copy(currentStep = s.currentStep + 1)
            persistLocalDraft()
            syncDraftToServer()
        }
    }

    fun previousStep() {
        val s = _state.value
        if (s.currentStep > 0) {
            _state.value = s.copy(currentStep = s.currentStep - 1)
            persistLocalDraft()
        }
    }

    private fun syncDraftToServer() {
        viewModelScope.launch {
            val request = buildRequest()
            if (request.title.isBlank()) return@launch
            val result = if (_state.value.experienceId == null) {
                repository.create(request)
            } else {
                repository.update(_state.value.experienceId!!, request)
            }
            if (result is Result.Success) {
                _state.value = _state.value.copy(experienceId = result.data.id)
                draftRepository.saveDraft(localDraftId, _state.value)
            }
        }
    }

    fun saveDraft(onSaved: (String) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            val request = buildRequest()
            val result = if (_state.value.experienceId == null) {
                repository.create(request)
            } else {
                repository.update(_state.value.experienceId!!, request)
            }
            when (result) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        experienceId = result.data.id,
                        isSaving = false
                    )
                    draftRepository.saveDraft(localDraftId, _state.value)
                    onSaved(result.data.id)
                }
                is Result.Error -> _state.value = _state.value.copy(isSaving = false, error = result.message)
            }
        }
    }

    fun publish(onPublished: (String) -> Unit) {
        viewModelScope.launch {
            _publishState.value = UiState.Loading
            saveDraftSync { id ->
                when (val result = repository.publish(id)) {
                    is Result.Success -> {
                        draftRepository.clearDraft(localDraftId)
                        if (localDraftId != ACTIVE_DRAFT_ID) {
                            draftRepository.clearDraft(ACTIVE_DRAFT_ID)
                        }
                        _publishState.value = UiState.Success(result.data.id)
                        onPublished(result.data.id)
                    }
                    is Result.Error -> _publishState.value = UiState.Error(result.message)
                }
            }
        }
    }

    private suspend fun saveDraftSync(onSaved: suspend (String) -> Unit) {
        val request = buildRequest()
        val result = if (_state.value.experienceId == null) {
            repository.create(request)
        } else {
            repository.update(_state.value.experienceId!!, request)
        }
        when (result) {
            is Result.Success -> {
                _state.value = _state.value.copy(experienceId = result.data.id)
                draftRepository.saveDraft(localDraftId, _state.value)
                onSaved(result.data.id)
            }
            is Result.Error -> _publishState.value = UiState.Error(result.message)
        }
    }

    private fun buildRequest() = CreateExperienceRequestDto(
        title = _state.value.title.ifBlank { "Untitled draft" },
        overview = _state.value.overview.ifBlank { null },
        destination = _state.value.destination.ifBlank { null },
        coverImageUrl = _state.value.coverImageUrl.ifBlank { null },
        timeline = _state.value.timeline.ifEmpty { null },
        budget = _state.value.budget.ifEmpty { null },
        accommodation = _state.value.accommodation.ifEmpty { null },
        food = _state.value.food.ifEmpty { null },
        transportation = _state.value.transportation.ifEmpty { null },
        gallery = _state.value.gallery.ifEmpty { null },
        tips = _state.value.tips.ifEmpty { null },
        packing = _state.value.packing.ifEmpty { null }
    )
}
