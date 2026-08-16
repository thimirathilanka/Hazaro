package com.example.hazaro.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hazaro.HazaroApp
import com.example.hazaro.data.auth.toAuthMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUp: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val succeeded: Boolean = false,
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = (application as HazaroApp).container.authRepository
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, error = null) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isSignUp = !it.isSignUp,
                confirmPassword = "",
                error = null,
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(error = "Email and password are required.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(error = "Password should be at least 6 characters.") }
            return
        }
        if (state.isSignUp && password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                if (state.isSignUp) {
                    authRepository.signUp(email, password)
                } else {
                    authRepository.signIn(email, password)
                }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, succeeded = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.toAuthMessage())
                }
            }
        }
    }
}
