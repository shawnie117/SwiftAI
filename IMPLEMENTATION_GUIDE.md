# Implementation Guide

## Quick Start Checklist

### ✅ Phase 1: Setup (COMPLETED)
- [x] Project structure created
- [x] All placeholder files created
- [x] Dependencies configured in `libs.versions.toml`
- [x] Build files updated with plugins and dependencies

### 📋 Phase 2: Configuration (DO THIS NEXT)

1. **Sync Gradle**
   - Open project in Android Studio
   - Click "Sync Now" when prompted
   - Wait for dependency download to complete

2. **Setup Firebase**
   - Visit: https://console.firebase.google.com/
   - Create/Select project
   - Add Android app: `com.swiftai.app`
   - Download `google-services.json`
   - Replace `app/google-services.json` with your file
   - Enable Authentication (Email/Password + Google)
   - Enable Cloud Firestore

3. **Update AndroidManifest.xml**
   Add these permissions:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```

   Update application tag:
   ```xml
   <application
       android:name=".SwiftAIApplication"
       ...>
   ```

### 📝 Phase 3: Implementation Order

Implement files in this order for best results:

#### Step 1: Domain Models (No dependencies)
1. `domain/model/User.kt`
2. `domain/model/Message.kt`
3. `domain/model/Chat.kt`
4. `domain/model/AIModel.kt`

#### Step 2: Data Layer - Local
1. `data/local/entity/UserEntity.kt`
2. `data/local/entity/MessageEntity.kt`
3. `data/local/entity/ChatEntity.kt`
4. `data/local/dao/UserDao.kt`
5. `data/local/dao/MessageDao.kt`
6. `data/local/dao/ChatDao.kt`
7. `data/local/database/AppDatabase.kt`

#### Step 3: Data Layer - Remote
1. `data/remote/dto/ChatRequest.kt`
2. `data/remote/dto/ChatResponse.kt`
3. `data/remote/api/SwiftAIApi.kt`
4. `data/remote/firebase/FirebaseAuthService.kt`
5. `data/remote/firebase/FirestoreService.kt`

#### Step 4: Data Layer - Repositories
1. `data/repository/AuthRepository.kt`
2. `data/repository/UserRepository.kt`
3. `data/repository/ChatRepository.kt`

#### Step 5: Dependency Injection
1. `di/NetworkModule.kt`
2. `di/DatabaseModule.kt`
3. `di/AppModule.kt`
4. `SwiftAIApplication.kt` (Add @HiltAndroidApp)

#### Step 6: Domain Use Cases
1. `domain/usecase/auth/LoginUseCase.kt`
2. `domain/usecase/auth/SignupUseCase.kt`
3. `domain/usecase/auth/GoogleSignInUseCase.kt`
4. `domain/usecase/chat/SendMessageUseCase.kt`
5. `domain/usecase/chat/GetChatsUseCase.kt`
6. `domain/usecase/chat/DeleteChatUseCase.kt`

#### Step 7: UI - Theme & Navigation
1. `ui/theme/Color.kt` (update if needed)
2. `ui/theme/Type.kt` (update if needed)
3. `ui/theme/Theme.kt` (update if needed)
4. `ui/navigation/Screen.kt`
5. `ui/navigation/NavGraph.kt`

#### Step 8: UI - Components
1. `ui/components/GradientButton.kt`
2. `ui/components/GlassmorphicCard.kt`
3. `ui/components/InputBar.kt`
4. `ui/components/MessageBubble.kt`
5. `ui/components/ChatListItem.kt`
6. `ui/components/TypingIndicator.kt`
7. `ui/components/NavigationDrawer.kt`

#### Step 9: UI - Screens & ViewModels
1. `ui/screens/splash/SplashScreen.kt`
2. `ui/screens/auth/LoginViewModel.kt`
3. `ui/screens/auth/LoginScreen.kt`
4. `ui/screens/auth/SignupViewModel.kt`
5. `ui/screens/auth/SignupScreen.kt`
6. `ui/screens/home/HomeViewModel.kt`
7. `ui/screens/home/HomeScreen.kt`
8. `ui/screens/chat/ChatViewModel.kt`
9. `ui/screens/chat/ChatScreen.kt`
10. `ui/screens/settings/SettingsViewModel.kt`
11. `ui/screens/settings/SettingsScreen.kt`

#### Step 10: Main Activity
1. `MainActivity.kt` (Update with navigation setup)

## Common Patterns

### ViewModel Structure
```kotlin
@HiltViewModel
class YourViewModel @Inject constructor(
    private val useCase: YourUseCase
) : ViewModel() {
    // State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Events
    fun onEvent(event: YourEvent) {
        // Handle events
    }
}
```

### Repository Pattern
```kotlin
class YourRepository @Inject constructor(
    private val localDataSource: YourDao,
    private val remoteDataSource: YourApi
) {
    fun getData(): Flow<Result<Data>> = flow {
        // Implementation
    }
}
```

### Use Case Pattern
```kotlin
class YourUseCase @Inject constructor(
    private val repository: YourRepository
) {
    operator fun invoke(params: Params): Flow<Result<Data>> {
        return repository.getData()
    }
}
```

## Testing

After implementation, create tests for:
- Unit tests for ViewModels
- Unit tests for Use Cases
- Unit tests for Repositories
- UI tests for Screens

## Resources

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Firebase for Android](https://firebase.google.com/docs/android/setup)
- [Retrofit](https://square.github.io/retrofit/)

## Notes

- All placeholder files have package declarations
- Each file has a TODO comment indicating where to add code
- Follow Kotlin coding conventions
- Use sealed classes for UI states and events
- Implement proper error handling
- Add loading states for async operations

