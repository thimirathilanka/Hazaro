package com.example.hazaro.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazaro.HazaroApp
import com.example.hazaro.R
import com.example.hazaro.data.model.Report
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val reports: List<Report> = emptyList(),
    val isLoadingReports: Boolean = true,
    val reportsError: String? = null,
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val userLocation: LatLng? = null,
    val hasLocationPermission: Boolean = false,
    val recenterNonce: Int = 0,
    val selectedReport: Report? = null,
    val showSignOutDialog: Boolean = false,
)

private data class HomeExtras(
    val selectedReport: Report? = null,
    val showSignOutDialog: Boolean = false,
    val userLocation: LatLng? = null,
    val hasLocationPermission: Boolean = false,
    val recenterNonce: Int = 0,
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as HazaroApp).container
    private val extras = MutableStateFlow(HomeExtras())

    val uiState: StateFlow<HomeUiState> = combine(
        container.reportRepository.observeReports(),
        container.authRepository.authState,
        extras,
    ) { reportsResult, user, extra ->
        HomeUiState(
            reports = reportsResult.getOrDefault(emptyList()),
            isLoadingReports = false,
            reportsError = reportsResult.exceptionOrNull()?.toReportsMessage(application),
            isSignedIn = user != null,
            userEmail = user?.email,
            userLocation = extra.userLocation,
            hasLocationPermission = extra.hasLocationPermission,
            recenterNonce = extra.recenterNonce,
            selectedReport = extra.selectedReport?.let { selected ->
                reportsResult.getOrDefault(emptyList()).find { it.id == selected.id } ?: selected
            },
            showSignOutDialog = extra.showSignOutDialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun onLocationPermissionResult(granted: Boolean) {
        extras.update { it.copy(hasLocationPermission = granted) }
        if (granted) refreshLocation()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            val location = container.locationClient.currentLocation() ?: return@launch
            extras.update {
                it.copy(
                    hasLocationPermission = true,
                    userLocation = LatLng(location.latitude, location.longitude),
                )
            }
        }
    }

    fun returnToMyLocation() {
        viewModelScope.launch {
            val location = container.locationClient.currentLocation() ?: return@launch
            extras.update {
                it.copy(
                    hasLocationPermission = true,
                    userLocation = LatLng(location.latitude, location.longitude),
                    recenterNonce = it.recenterNonce + 1,
                )
            }
        }
    }

    fun selectReport(report: Report?) {
        extras.update { it.copy(selectedReport = report) }
    }

    fun showSignOutDialog(show: Boolean) {
        extras.update { it.copy(showSignOutDialog = show) }
    }

    fun signOut() {
        container.authRepository.signOut()
        extras.update { it.copy(showSignOutDialog = false) }
    }

    fun currentUser(): FirebaseUser? = container.authRepository.currentUser
}

private fun Throwable.toReportsMessage(application: Application): String {
    val raw = localizedMessage.orEmpty()
    return if (
        raw.contains("PERMISSION_DENIED", ignoreCase = true) ||
        raw.contains("Missing or insufficient permissions", ignoreCase = true)
    ) {
        application.getString(R.string.reports_permission_denied)
    } else {
        raw.ifBlank { application.getString(R.string.reports_error) }
    }
}
