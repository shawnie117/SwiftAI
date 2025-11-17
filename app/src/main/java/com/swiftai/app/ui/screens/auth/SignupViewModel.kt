package com.swiftai.app.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.swiftai.app.domain.usecase.auth.GoogleSignInUseCase
import com.swiftai.app.domain.usecase.auth.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase  // ADD THIS
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, error = null)
    }

    fun onSignup() {
        val name = _uiState.value.name.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        // Validation
        when {
            name.isEmpty() -> {
                _uiState.value = _uiState.value.copy(error = "Name is required")
                return
            }
            name.length < 2 -> {
                _uiState.value = _uiState.value.copy(error = "Name must be at least 2 characters")
                return
            }
            email.isEmpty() -> {
                _uiState.value = _uiState.value.copy(error = "Email is required")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.value = _uiState.value.copy(error = "Invalid email format")
                return
            }
            password.isEmpty() -> {
                _uiState.value = _uiState.value.copy(error = "Password is required")
                return
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
                return
            }
            confirmPassword.isEmpty() -> {
                _uiState.value = _uiState.value.copy(error = "Please confirm your password")
                return
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(error = "Passwords don't match")
                return
            }
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = signupUseCase(email, password, name)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                    Log.d("SignupViewModel", "Signup successful")
                } else {
                    val errorMessage = when {
                        result.exceptionOrNull()?.message?.contains("already in use") == true ->
                            "This email is already registered"
                        result.exceptionOrNull()?.message?.contains("network") == true ->
                            "Network error. Check your connection"
                        result.exceptionOrNull()?.message?.contains("weak-password") == true ->
                            "Password is too weak"
                        else -> result.exceptionOrNull()?.message ?: "Signup failed"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                    Log.e("SignupViewModel", "Signup error: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An unexpected error occurred: ${e.localizedMessage}"
                )
                Log.e("SignupViewModel", "Exception during signup", e)
            }
        }
    }

    // ADD THIS NEW FUNCTION FOR GOOGLE SIGN-UP
    fun onGoogleSignUp(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = googleSignInUseCase(account)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                    Log.d("SignupViewModel", "Google sign-up successful")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Google sign-up failed"
                    )
                    Log.e("SignupViewModel", "Google sign-up error: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Google sign-up error"
                )
                Log.e("SignupViewModel", "Exception: ${e.message}", e)
            }
        }
    }
}

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
