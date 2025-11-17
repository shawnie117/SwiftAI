package com.swiftai.app.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.swiftai.app.domain.usecase.auth.GoogleSignInUseCase
import com.swiftai.app.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onLogin() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // Enhanced validation with detailed messages
        when {
            email.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    error = "Email is required",
                    passwordError = null
                )
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.value = _uiState.value.copy(
                    error = "Please enter a valid email address",
                    passwordError = null
                )
                return
            }
            password.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    error = null,
                    passwordError = "Password is required"
                )
                return
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(
                    error = null,
                    passwordError = "Password must be at least 6 characters"
                )
                return
            }
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null, passwordError = null)

                val result = loginUseCase(email, password)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: ""
                    val (mainError, passError) = when {
                        errorMessage.contains("password", ignoreCase = true) ||
                                errorMessage.contains("INVALID_PASSWORD") ->
                            null to "Incorrect password. Please try again."
                        errorMessage.contains("user not found", ignoreCase = true) ||
                                errorMessage.contains("no user", ignoreCase = true) ||
                                errorMessage.contains("INVALID_EMAIL") ->
                            "No account found with this email" to null
                        errorMessage.contains("network", ignoreCase = true) ->
                            "Network error. Check your connection" to null
                        errorMessage.contains("too many", ignoreCase = true) ->
                            "Too many failed attempts. Try again later" to null
                        else -> "Login failed. Please try again" to null
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = mainError,
                        passwordError = passError
                    )
                    Log.e("LoginViewModel", "Login error: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An unexpected error occurred"
                )
                Log.e("LoginViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    fun onGoogleSignIn(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null, passwordError = null)

                val result = googleSignInUseCase(account)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Google sign in failed"
                    )
                    Log.e("LoginViewModel", "Google sign-in error: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Google sign-in error"
                )
                Log.e("LoginViewModel", "Exception: ${e.message}", e)
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val passwordError: String? = null  // NEW: Separate password error
)
