package com.swiftai.app.domain.usecase.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.swiftai.app.data.repository.AuthRepository
import com.swiftai.app.domain.model.User
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(account: GoogleSignInAccount): Result<User> {
        return authRepository.signInWithGoogle(account)
    }
}
