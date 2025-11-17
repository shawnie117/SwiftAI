package com.swiftai.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object Chat : Screen("chat/{chatId}") {
        fun createRoute(chatId: String) = "chat/$chatId"
    }
    object Settings : Screen("settings")
    object AITools : Screen("ai_tools")
    object AIToolDetail : Screen("ai_tool/{toolId}") {
        fun createRoute(toolId: String) = "ai_tool/$toolId"
    }
    object Subscription : Screen("subscription")
}
