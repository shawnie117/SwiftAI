package com.swiftai.app.domain.usecase.auth

import com.swiftai.app.data.repository.AuthRepository
import com.swiftai.app.domain.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signInWithEmail(email, password)
    }
}
