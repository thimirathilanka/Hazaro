package com.example.hazaro.ui.report

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazaro.HazaroApp
import com.example.hazaro.data.model.DisasterType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddReportUiState(
    val type: DisasterType = DisasterType.LANDSLIDE,
    val description: String = "",
    val photoUri: Uri? = null,
    val location: LatLng? = null,
    val locationIsManual: Boolean = false,
    val isLoadingLocation: Boolean = true,
    val recenterNonce: Int = 0,
    val isSubmitting: Boolean = false,
    val statusMessage: String? = null,
    val error: String? = null,
    val saved: Boolean = false,
    val requiresAuth: Boolean = false,
)

class AddReportViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as HazaroApp).container
    private val _uiState = MutableStateFlow(
        AddReportUiState(requiresAuth = container.authRepository.currentUser == null),
    )
    val uiState: StateFlow<AddReportUiState> = _uiState.asStateFlow()

    init {
        refreshLocation()
    }

    fun onTypeSelected(type: DisasterType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, error = null) }
    }

    fun onPhotoPicked(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri, error = null) }
    }

    fun onLocationPicked(latLng: LatLng) {
        _uiState.update {
            it.copy(
                location = latLng,
                locationIsManual = true,
                isLoadingLocation = false,
                error = null,
            )
        }
    }

    fun returnToMyLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            val location = container.locationClient.currentLocation()
            _uiState.update {
                if (location == null) {
                    it.copy(isLoadingLocation = false)
                } else {
                    it.copy(
                        isLoadingLocation = false,
                        location = LatLng(location.latitude, location.longitude),
                        locationIsManual = false,
                        recenterNonce = it.recenterNonce + 1,
                    )
                }
            }
        }
    }

    fun refreshLocation() {
        viewModelScope.launch {
            if (_uiState.value.locationIsManual) return@launch
            _uiState.update { it.copy(isLoadingLocation = true) }
            val location = container.locationClient.currentLocation()
            _uiState.update {
                if (it.locationIsManual) {
                    it.copy(isLoadingLocation = false)
                } else {
                    it.copy(
                        isLoadingLocation = false,
                        location = location?.let { loc -> LatLng(loc.latitude, loc.longitude) },
                    )
                }
            }
        }
    }

    fun submit() {
        val user = container.authRepository.currentUser
        if (user == null) {
            _uiState.update { it.copy(requiresAuth = true) }
            return
        }
        val state = _uiState.value
        val location = state.location
        val photo = state.photoUri
        when {
            location == null -> _uiState.update { it.copy(error = "Location is required to submit a report.") }
            else -> viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isSubmitting = true,
                        error = null,
                        statusMessage = if (photo != null) "Uploading photo…" else "Saving report…",
                    )
                }
                runCatching {
                    val photoUrl = if (photo != null) {
                        container.cloudinaryUploader.upload(
                            getApplication(),
                            photo,
                        )
                    } else {
                        ""
                    }
                    _uiState.update { it.copy(statusMessage = "Saving report…") }
                    container.reportRepository.addReport(
                        type = state.type,
                        description = state.description,
                        photoUrl = photoUrl,
                        lat = location.latitude,
                        lng = location.longitude,
                        reporterId = user.uid,
                        reporterEmail = user.email.orEmpty(),
                    )
                }.onSuccess {
                    _uiState.update {
                        it.copy(isSubmitting = false, statusMessage = null, saved = true)
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            statusMessage = null,
                            error = error.localizedMessage ?: "Could not save the report.",
                        )
                    }
                }
            }
        }
    }
}
