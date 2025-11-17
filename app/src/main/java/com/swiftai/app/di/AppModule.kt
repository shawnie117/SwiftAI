package com.swiftai.app.di

import android.content.Context
import com.swiftai.app.data.remote.api.SwiftAIApi
import com.swiftai.app.data.remote.firebase.FirebaseAuthService
import com.swiftai.app.data.remote.firebase.FirestoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthService(
        @ApplicationContext context: Context
    ): FirebaseAuthService {
        return FirebaseAuthService(context)
    }

    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService {
        return FirestoreService()
    }

    @Provides
    @Singleton
    fun provideSwiftAIApi(): SwiftAIApi {
        return SwiftAIApi()
    }
}
