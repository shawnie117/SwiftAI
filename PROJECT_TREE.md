# SwiftAI - Complete Project Tree Structure

**Generated on:** November 18, 2025

```
SwiftAI/
│
├── 📄 Project Configuration Files
│   ├── .gitignore
│   ├── build.gradle.kts (Project-level)
│   ├── settings.gradle.kts
│   ├── gradle.properties
│   ├── local.properties
│   ├── gradlew
│   └── gradlew.bat
│
├── 📚 Documentation
│   ├── ALL_ERRORS_FIXED_REPORT.md
│   ├── CHAT_HISTORY_DEBUG_GUIDE.md
│   ├── CHAT_HISTORY_FIXED.md
│   ├── CHATGPT_STYLE_COMPLETE.md
│   ├── COMPLETE_PROJECT_TREE.md
│   ├── ENABLE_API_NOW.md
│   ├── ERRORS_FIXED_SUMMARY.md
│   ├── FIX_INSTRUCTIONS.md
│   ├── GEMINI_API_FIX.md
│   ├── GEMINI_API_KEY_FIX.md
│   ├── GEMINI_CONNECTION_FIX.md
│   ├── GEMINI_FIXED_FINAL.md
│   ├── HILT_FIX_APPLIED.md
│   ├── IMPLEMENTATION_GUIDE.md
│   ├── LOGIN_CRASH_FIX.md
│   ├── MESSAGE_ALIGNMENT_FIXED.md
│   ├── MESSAGE_BUBBLE_COMPLETE.md
│   └── PROJECT_STRUCTURE.md
│
├── 📁 gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
└── 📱 app/
    ├── .gitignore
    ├── build.gradle.kts (App-level)
    ├── proguard-rules.pro
    ├── google-services.json (Firebase config)
    │
    └── src/
        ├── 🧪 androidTest/
        │   └── java/com/swiftai/app/
        │       └── ExampleInstrumentedTest.kt
        │
        ├── 🧪 test/
        │   └── java/com/swiftai/app/
        │       └── ExampleUnitTest.kt
        │
        └── 🚀 main/
            ├── AndroidManifest.xml
            │
            ├── 🎨 res/
            │   ├── drawable/
            │   │   ├── ic_launcher_background.xml
            │   │   ├── ic_launcher_foreground.xml
            │   │   └── splash.xml
            │   │
            │   ├── mipmap-anydpi-v26/
            │   │   ├── ic_launcher.xml
            │   │   └── ic_launcher_round.xml
            │   │
            │   ├── mipmap-hdpi/
            │   │   ├── ic_launcher.webp
            │   │   └── ic_launcher_round.webp
            │   │
            │   ├── mipmap-mdpi/
            │   │   ├── ic_launcher.webp
            │   │   └── ic_launcher_round.webp
            │   │
            │   ├── mipmap-xhdpi/
            │   │   ├── ic_launcher.webp
            │   │   └── ic_launcher_round.webp
            │   │
            │   ├── mipmap-xxhdpi/
            │   │   ├── ic_launcher.webp
            │   │   └── ic_launcher_round.webp
            │   │
            │   ├── mipmap-xxxhdpi/
            │   │   ├── ic_launcher.webp
            │   │   └── ic_launcher_round.webp
            │   │
            │   ├── values/
            │   │   ├── colors.xml
            │   │   ├── strings.xml
            │   │   ├── styles.xml
            │   │   └── themes.xml
            │   │
            │   └── xml/
            │       ├── backup_rules.xml
            │       └── data_extraction_rules.xml
            │
            └── 💻 java/com/swiftai/app/
                │
                ├── 🏁 Entry Points
                │   ├── MainActivity.kt
                │   └── SwiftAIApplication.kt
                │
                ├── 💾 data/
                │   │
                │   ├── local/
                │   │   ├── dao/
                │   │   │   ├── ChatDao.kt
                │   │   │   ├── MessageDao.kt
                │   │   │   └── UserDao.kt
                │   │   │
                │   │   ├── database/
                │   │   │   └── AppDatabase.kt
                │   │   │
                │   │   └── entity/
                │   │       ├── ChatEntity.kt
                │   │       ├── MessageEntity.kt
                │   │       └── UserEntity.kt
                │   │
                │   ├── remote/
                │   │   ├── api/
                │   │   │   ├── GeminiApi.kt
                │   │   │   ├── HuggingFaceApi.kt
                │   │   │   └── SwiftAIApi.kt
                │   │   │
                │   │   ├── dto/
                │   │   │   ├── ChatRequest.kt
                │   │   │   └── ChatResponse.kt
                │   │   │
                │   │   └── firebase/
                │   │       ├── FirebaseAuthService.kt
                │   │       └── FirestoreService.kt
                │   │
                │   └── repository/
                │       ├── AuthRepository.kt
                │       ├── ChatRepository.kt
                │       └── UserRepository.kt
                │
                ├── 🎯 domain/
                │   │
                │   ├── model/
                │   │   ├── AIModel.kt
                │   │   ├── AITool.kt
                │   │   ├── Chat.kt
                │   │   ├── Message.kt
                │   │   └── User.kt
                │   │
                │   └── usecase/
                │       ├── auth/
                │       │   ├── GoogleSignInUseCase.kt
                │       │   ├── LoginUseCase.kt
                │       │   └── SignupUseCase.kt
                │       │
                │       └── chat/
                │           ├── DeleteChatUseCase.kt
                │           ├── GetChatsUseCase.kt
                │           └── SendMessageUseCase.kt
                │
                ├── 💉 di/ (Dependency Injection)
                │   ├── AppModule.kt
                │   ├── DatabaseModule.kt
                │   └── NetworkModule.kt
                │
                ├── 🌐 network/
                │   └── ChatApi.kt
                │
                └── 🎨 ui/
                    │
                    ├── components/
                    │   ├── ChatBubble.kt
                    │   ├── ChatListItem.kt
                    │   ├── GlassmorphicCard.kt
                    │   ├── GradientButton.kt
                    │   ├── InputBar.kt
                    │   ├── MessageBubble.kt
                    │   ├── NavigationDrawer.kt
                    │   ├── SuggestionChips.kt
                    │   └── TypingIndicator.kt
                    │
                    ├── navigation/
                    │   ├── NavGraph.kt
                    │   └── Screen.kt
                    │
                    ├── screens/
                    │   │
                    │   ├── aitools/
                    │   │   ├── AIToolDetailScreen.kt
                    │   │   ├── AIToolsScreen.kt
                    │   │   └── AIToolViewModel.kt
                    │   │
                    │   ├── auth/
                    │   │   ├── LoginScreen.kt
                    │   │   ├── LoginViewModel.kt
                    │   │   ├── SignupScreen.kt
                    │   │   └── SignupViewModel.kt
                    │   │
                    │   ├── chat/
                    │   │   ├── ChatListScreen.kt
                    │   │   ├── ChatScreen.kt
                    │   │   └── ChatViewModel.kt
                    │   │
                    │   ├── home/
                    │   │   ├── HomeScreen.kt
                    │   │   └── HomeViewModel.kt
                    │   │
                    │   ├── settings/
                    │   │   ├── SettingsScreen.kt
                    │   │   └── SettingsViewModel.kt
                    │   │
                    │   ├── splash/
                    │   │   └── SplashScreen.kt
                    │   │
                    │   └── subscription/
                    │       ├── SubscriptionScreen.kt
                    │       └── SubscriptionViewModel.kt
                    │
                    └── theme/
                        ├── Color.kt
                        ├── Theme.kt
                        └── Type.kt
```

## 📊 Project Statistics

### Architecture
- **Pattern:** MVVM (Model-View-ViewModel) with Clean Architecture
- **DI Framework:** Hilt (Dagger)
- **UI Framework:** Jetpack Compose
- **Database:** Room (Local) + Firestore (Remote)

### Key Features
1. **Authentication** - Firebase Auth (Email/Password + Google Sign-In)
2. **Chat System** - Real-time messaging with AI (Gemini API)
3. **AI Tools** - Multiple AI-powered tools for different tasks
4. **Subscriptions** - Free, Pro, and Max tier system
5. **Settings** - User preferences and account management

### File Count Summary
- **Kotlin Source Files:** 71
- **XML Resources:** 21
- **Gradle Files:** 4
- **Documentation Files:** 16

### Main Packages
```
com.swiftai.app/
├── data/          # Data layer (repositories, DAOs, APIs)
├── domain/        # Business logic (models, use cases)
├── ui/            # Presentation layer (screens, components, ViewModels)
├── di/            # Dependency injection modules
└── network/       # Network-related utilities
```

### Technology Stack
- **Language:** Kotlin
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Compose Version:** Latest stable
- **Firebase:** Auth, Firestore
- **AI Integration:** Google Gemini API, HuggingFace API
- **Testing:** JUnit, Espresso

---
*Generated automatically for SwiftAI project*

