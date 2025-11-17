package com.swiftai.app.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadCurrentTier()
    }

    private fun loadCurrentTier() {
        viewModelScope.launch {
            userRepository.getUserFlow(currentUserId).collect { user ->
                _uiState.value = _uiState.value.copy(
                    currentTier = user?.subscriptionTier ?: "free"
                )
            }
        }
    }

    fun subscribeTo(tier: String) {
        // TODO: Implement Razorpay/Stripe payment here
        // For now, just show that it's called
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessingPayment = true
            )

            // After payment success, update tier
            // userRepository.updateSubscriptionTier(currentUserId, tier)
        }
    }
}

data class SubscriptionUiState(
    val currentTier: String = "free",
    val isProcessingPayment: Boolean = false
)
