package com.swiftai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swiftai.app.ui.screens.auth.LoginScreen
import com.swiftai.app.ui.screens.auth.SignupScreen
import com.swiftai.app.ui.screens.chat.ChatScreen
import com.swiftai.app.ui.screens.home.HomeScreen
import com.swiftai.app.ui.screens.settings.SettingsScreen
import com.swiftai.app.ui.screens.splash.SplashScreen
import com.swiftai.app.ui.screens.aitools.AIToolsScreen
import com.swiftai.app.ui.screens.aitools.AIToolDetailScreen
import com.swiftai.app.ui.screens.subscription.SubscriptionScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Signup.route) {
            SignupScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(navController, chatId)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screen.AITools.route) {
            AIToolsScreen(navController, userTier = "free")
        }

        composable(
            route = Screen.AIToolDetail.route,
            arguments = listOf(navArgument("toolId") { type = NavType.StringType })
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
            AIToolDetailScreen(navController, toolId)
        }

        composable(Screen.Subscription.route) {
            SubscriptionScreen(navController, currentPlan = "free")
        }
    }
}
