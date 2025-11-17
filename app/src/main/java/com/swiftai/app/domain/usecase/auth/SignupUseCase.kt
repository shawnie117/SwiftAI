package com.swiftai.app.domain.usecase.auth

import com.swiftai.app.data.repository.AuthRepository
import com.swiftai.app.domain.model.User
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return authRepository.signUpWithEmail(email, password, displayName)
    }
}
