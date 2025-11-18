<div align="center">

# ⚡ SwiftAI

**Your Intelligent AI Chat Companion**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.0-green.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com)

[Features](#-features) • [Screenshots](#-screenshots) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [Architecture](#-architecture) • [Contributing](#-contributing)

</div>

---

## 📖 About

**SwiftAI** is a modern Android chat application that brings the power of AI to your fingertips. Built entirely with Jetpack Compose and following clean architecture principles, SwiftAI provides an intuitive and beautiful interface for conversing with AI models while maintaining a complete chat history synchronized across devices using Firebase.

### 🎯 Key Highlights

- **Modern UI/UX**: Sleek, WhatsApp/Telegram-inspired chat interface with smooth animations
- **AI-Powered**: Integration with Gemini AI and custom SwiftAI backend
- **Real-time Sync**: Firebase Firestore for instant message synchronization
- **Offline Support**: Local Room database for offline access
- **Subscription Tiers**: Free, Standard, Pro, and Max tiers with different AI models
- **AI Tools**: Specialized AI tools for various tasks
- **Secure Authentication**: Firebase Auth with email/password and Google Sign-In

---

## ✨ Features

### 🤖 AI Chat
- Real-time conversational AI with multiple model support
- Message history synchronized across devices
- Typing indicators and smooth animations
- Support for multiple chat sessions
- AI response formatting with proper text rendering

### 🎨 Beautiful UI
- Modern Material 3 Design
- Dark theme optimized for OLED displays
- Gradient backgrounds and glassmorphic effects
- Smooth animations and transitions
- Responsive layouts for all screen sizes

### 🔐 Authentication
- Email/Password authentication
- Google Sign-In integration
- Secure Firebase backend
- User profile management

### 💾 Data Management
- Firebase Firestore for cloud storage
- Room database for local caching
- Automatic sync and conflict resolution
- Chat history persistence

### 🛠️ AI Tools
- Multiple specialized AI tools
- Tool customization and favorites
- Quick access from home screen

### 📊 Subscription System
- Free tier with basic features
- Standard, Pro, and Max tiers
- Different AI models per tier
- Usage tracking and limits

---

## 📱 Screenshots

<div align="center">

| Login | Home | Chat |
|-------|------|------|
| ![Login](screenshots/login.png) | ![Home](screenshots/home.png) | ![Chat](screenshots/chat.png) |

| AI Tools | Settings | Subscription |
|----------|----------|--------------|
| ![Tools](screenshots/tools.png) | ![Settings](screenshots/settings.png) | ![Subscription](screenshots/subscription.png) |

</div>

> **Note**: Add screenshots to the `screenshots/` folder in your repository

---

## 🛠️ Tech Stack

### Core
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Architecture & Patterns
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt (Dagger)
- **Reactive Programming**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose

### Backend & Storage
- **Authentication**: Firebase Auth
- **Cloud Database**: Firebase Firestore
- **Local Database**: Room
- **Preferences**: DataStore

### Networking
- **HTTP Client**: OkHttp
- **JSON Parsing**: Gson
- **AI SDK**: Google Generative AI (Gemini)

### UI & Design
- **Design System**: Material 3
- **Icons**: Material Icons Extended
- **Image Loading**: Coil
- **Animations**: Compose Animations

### Build & Development
- **Build System**: Gradle (Kotlin DSL)
- **Code Generation**: KSP (Kotlin Symbol Processing)
- **Version Catalogs**: Gradle Version Catalogs

---

## 🚀 Getting Started

### Prerequisites

- **JDK 17** or higher
- **Android Studio** Hedgehog (2023.1.1) or newer
- **Android SDK** with API 34
- **Firebase Account** (for google-services.json)
- **Gemini API Key** (optional, for direct Gemini integration)

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/SwiftAI.git
cd SwiftAI
```

2. **Set up Firebase**

   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Add an Android app with package name: `com.swiftai.app`
   - Download `google-services.json`
   - Place it in `app/` directory

3. **Configure API Keys**

   Create a `local.properties` file in the project root and add:

```properties
# Required for BuildConfig
GEMINI_API_KEY=your_gemini_api_key_here

# SDK location (auto-generated by Android Studio)
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

4. **Build and Run**

   **Option A: Using Android Studio**
   - Open the project in Android Studio
   - Wait for Gradle sync to complete
   - Click **Run** (Shift+F10) or the play button
   - Select your device/emulator

   **Option B: Using Command Line**

```powershell
# Windows PowerShell
.\gradlew clean assembleDebug
.\gradlew installDebug

# Or build and install in one command
.\gradlew installDebug
```

---

## 🏗️ Architecture

SwiftAI follows **Clean Architecture** principles with clear separation of concerns:

```
app/
├── data/                          # Data Layer
│   ├── local/                     # Local data sources
│   │   ├── dao/                   # Room DAOs
│   │   ├── database/              # Room Database
│   │   └── entity/                # Room Entities
│   ├── remote/                    # Remote data sources
│   │   ├── api/                   # API clients
│   │   │   ├── GeminiApi.kt       # Gemini AI integration
│   │   │   └── SwiftAIApi.kt      # Custom backend API
│   │   ├── dto/                   # Data Transfer Objects
│   │   └── firebase/              # Firebase services
│   └── repository/                # Repository implementations
│       ├── AuthRepository.kt
│       ├── ChatRepository.kt
│       └── UserRepository.kt
│
├── domain/                        # Domain Layer
│   ├── model/                     # Domain models
│   │   ├── User.kt
│   │   ├── Chat.kt
│   │   ├── Message.kt
│   │   └── AIModel.kt
│   └── usecase/                   # Business logic
│       ├── auth/                  # Auth use cases
│       └── chat/                  # Chat use cases
│
├── ui/                            # Presentation Layer
│   ├── components/                # Reusable UI components
│   │   ├── MessageBubble.kt       # Chat message bubble
│   │   ├── SuggestionChips.kt     # Suggestion chips
│   │   ├── TypingIndicator.kt     # AI typing animation
│   │   └── ...
│   ├── screens/                   # Screen composables
│   │   ├��─ splash/
│   │   ├── auth/                  # Login & Signup
│   │   ├── home/                  # Home screen
│   │   ├── chat/                  # Chat screen
│   │   ├── settings/              # Settings
│   │   ├── aitools/               # AI Tools
│   │   └── subscription/          # Subscription
│   ├── theme/                     # App theming
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── navigation/                # Navigation
│       ├── NavGraph.kt
│       └── Screen.kt
│
├── di/                            # Dependency Injection
│   ├── AppModule.kt               # App-level dependencies
│   ├── DatabaseModule.kt          # Database dependencies
│   └── NetworkModule.kt           # Network dependencies
│
├── SwiftAIApplication.kt          # Application class
└── MainActivity.kt                # Main activity

```

### Data Flow

```
UI Layer (Composables)
    ↓ User Actions
ViewModel (State Management)
    ↓ Business Logic
Use Cases (Optional)
    ↓ Data Operations
Repository (Single Source of Truth)
    ↓                    ↓
Remote Data Source   Local Data Source
(Firebase/API)       (Room Database)
```

---

## 🔑 Key Components

### 1. **Chat System**
- **ChatScreen**: Main chat UI with message list and input
- **ChatViewModel**: Manages chat state and message operations
- **ChatRepository**: Handles message persistence and AI communication
- **MessageBubble**: Reusable component for chat messages

### 2. **AI Integration**
- **GeminiApi**: Direct integration with Gemini AI
- **SwiftAIApi**: Custom backend API with multiple models
- **Model Selection**: Support for different AI models based on subscription

### 3. **Authentication**
- **Firebase Auth**: Email/password and Google Sign-In
- **AuthRepository**: Centralized auth operations
- **User Management**: Profile, subscription, and preferences

### 4. **Database**
- **Firestore**: Real-time cloud database for messages and chats
- **Room**: Local SQLite database for offline support
- **Sync Logic**: Automatic synchronization between local and remote

---

## 🎨 Design System

### Color Palette

```kotlin
// Primary Colors
Purple = #8249FF
Cyan = #09DAC6
Amber = #FFB84D

// Background
Background = #0F1115
Surface = #17191D
SurfaceVariant = #1E2128

// Text
TextPrimary = #FFFFFF
TextSecondary = #B0B3BA

// Message Bubbles
UserBubble = #6C63FF (Gradient to #8249FF)
AIBubble = #232332
```

### Typography

- **Font Family**: Default System Font (Roboto)
- **Sizes**: 12sp - 34sp
- **Weights**: Normal, Medium, Bold

---

## 🧪 Testing

### Run Unit Tests

```powershell
.\gradlew test
```

### Run Instrumented Tests

```powershell
.\gradlew connectedAndroidTest
```

### Code Coverage

```powershell
.\gradlew jacocoTestReport
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. **Missing GEMINI_API_KEY**
**Error**: `Gemini API key not configured`

**Solution**: Add `GEMINI_API_KEY` to `local.properties`:
```properties
GEMINI_API_KEY=your_api_key_here
```

#### 2. **Firebase Errors**
**Error**: `google-services.json not found`

**Solution**: Download `google-services.json` from Firebase Console and place in `app/` folder

#### 3. **Build Config Error**
**Error**: `BuildConfig cannot be found`

**Solution**: Ensure `buildFeatures { buildConfig = true }` in `app/build.gradle.kts`

#### 4. **Hilt Compilation Errors**
**Error**: `Dagger/MissingBinding`

**Solution**: 
- Clean and rebuild: `.\gradlew clean build`
- Ensure all `@Inject` constructors are present
- Check DI modules for missing `@Provides` methods

#### 5. **Messages Not Appearing**
**Issue**: Messages sent but not visible in chat

**Solution**: 
- Check Firebase Firestore rules
- Verify network connectivity
- Check logcat for error messages
- Ensure `isUser` field is correctly set in messages

#### 6. **AI Response Not Formatted**
**Issue**: AI responses appear as plain text without formatting

**Solution**: 
- Check backend response format
- Ensure proper JSON parsing in `GeminiApi.kt` and `SwiftAIApi.kt`
- Verify message content rendering in `MessageBubble.kt`

---

## 📝 Configuration

### Backend URL Configuration

The app uses a backend API hosted at `https://shawniii-swiftai-api.hf.space`. To change this:

1. Open `GeminiApi.kt` and update:
```kotlin
private val baseUrl = "https://your-backend-url.com"
```

2. Open `SwiftAIApi.kt` and update:
```kotlin
private val defaultBaseUrl = "https://your-backend-url.com"
```

### Firebase Configuration

1. **Authentication**: Enable Email/Password and Google Sign-In in Firebase Console
2. **Firestore**: Create collections `users`, `chats`, and `messages`
3. **Security Rules**: Set appropriate Firestore security rules

Example Firestore Rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /chats/{chatId} {
      allow read, write: if request.auth != null && 
        resource.data.userId == request.auth.uid;
    }
    
    match /messages/{messageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 🎤 Presentation Guide

### Key Talking Points

1. **Modern Android Development**
   - 100% Kotlin
   - Jetpack Compose for declarative UI
   - Material 3 Design
   - Clean Architecture

2. **AI Integration**
   - Multiple AI model support
   - Real-time responses
   - Conversation history
   - Typing indicators

3. **Cloud & Offline**
   - Firebase for real-time sync
   - Room for offline storage
   - Seamless sync mechanism

4. **User Experience**
   - Smooth animations
   - Intuitive navigation
   - Beautiful dark theme
   - Responsive design

### Demo Flow (5-7 minutes)

**1. Introduction (30 seconds)**
   - "SwiftAI is an AI chat application built with modern Android technologies"
   - Show app icon and branding

**2. Authentication (1 minute)**
   - Launch app → Splash screen
   - Show login screen UI
   - Demonstrate Google Sign-In or Email login
   - Highlight smooth transitions

**3. Home Screen (1 minute)**
   - Show chat history list
   - Point out Material 3 design elements
   - Demonstrate navigation drawer
   - Show subscription tier badge

**4. Chat Experience (2 minutes)**
   - Create a new chat
   - Type a message: "What is artificial intelligence?"
   - Show typing indicator animation
   - Highlight message bubble design (user vs AI)
   - Show AI response appearing
   - Demonstrate smooth scrolling

**5. Features (1-2 minutes)**
   - AI Tools screen
   - Settings and customization
   - Subscription tiers
   - Chat history persistence

**6. Technical Deep Dive (1-2 minutes)**
   - Quick code walkthrough:
     - Architecture diagram
     - Show key files (MainActivity, NavGraph, ChatViewModel)
     - Highlight Hilt DI
     - Show Firebase integration
   - Mention testing and CI/CD ready

**7. Conclusion (30 seconds)**
   - Recap key features
   - Future roadmap
   - Q&A

### Technical Highlights for Presentation

- **Single Activity Architecture** with Compose Navigation
- **Reactive UI** with StateFlow and collectAsState
- **Dependency Injection** with Hilt for testability
- **Repository Pattern** for data abstraction
- **Firebase Integration** for real-time features
- **Custom UI Components** with Compose

### Presentation Tips

1. **Prepare Screenshots/Screen Recording**: In case live demo fails
2. **Have Backup Device**: Keep an emulator ready
3. **Pre-load Data**: Have some chat history already in the app
4. **Highlight Animations**: Mention smooth transitions and typing indicators
5. **Code Snippets**: Prepare key code snippets to show (ViewModel, Composable)

---

## 🚧 Roadmap

### Upcoming Features

- [ ] Voice input and text-to-speech
- [ ] Image generation with AI
- [ ] Markdown rendering in messages
- [ ] Message search and filtering
- [ ] Export chat history (PDF, TXT)
- [ ] Multi-language support (i18n)
- [ ] Dark/Light theme toggle
- [ ] Chat sharing
- [ ] Message reactions and emoji
- [ ] Push notifications for responses
- [ ] Widget support
- [ ] Wear OS companion app

### Technical Improvements

- [ ] Unit test coverage (80%+)
- [ ] UI tests with Compose Testing
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Performance monitoring (Firebase Performance)
- [ ] Crash reporting (Crashlytics)
- [ ] Analytics integration
- [ ] ProGuard/R8 optimization
- [ ] Code documentation (KDoc)

---

## 👥 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features
- Ensure code passes all existing tests
- Use Compose best practices

### Pull Request Guidelines

- Provide clear description of changes
- Include screenshots/videos for UI changes
- Reference related issues
- Ensure all tests pass
- Update documentation if needed

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 SwiftAI

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgments

- [Google Gemini AI](https://ai.google.dev/) for AI capabilities
- [Firebase](https://firebase.google.com/) for backend infrastructure
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI toolkit
- [Material Design](https://m3.material.io/) for design guidelines
- [Square](https://square.github.io/) for OkHttp and other libraries
- [Coil](https://coil-kt.github.io/coil/) for image loading
- All open-source contributors and the Android community

---

## 📧 Contact

**Project Maintainer**: Shawn

- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)

**Project Link**: [https://github.com/yourusername/SwiftAI](https://github.com/yourusername/SwiftAI)

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/yourusername/SwiftAI?style=social)
![GitHub forks](https://img.shields.io/github/forks/yourusername/SwiftAI?style=social)
![GitHub issues](https://img.shields.io/github/issues/yourusername/SwiftAI)
![GitHub pull requests](https://img.shields.io/github/issues-pr/yourusername/SwiftAI)
![GitHub last commit](https://img.shields.io/github/last-commit/yourusername/SwiftAI)

---

<div align="center">

Made with ❤️ using Kotlin & Jetpack Compose

⭐ **Star this repo if you find it helpful!** ⭐

[Report Bug](https://github.com/yourusername/SwiftAI/issues) • [Request Feature](https://github.com/yourusername/SwiftAI/issues) • [Documentation](https://github.com/yourusername/SwiftAI/wiki)

</div>

