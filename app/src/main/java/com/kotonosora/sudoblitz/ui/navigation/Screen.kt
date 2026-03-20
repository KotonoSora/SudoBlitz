package com.kotonosora.sudoblitz.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("game")
    object Result : Screen("result")
    object Shop : Screen("shop")
    object BoostSelection : Screen("boost_selection")
    object Progress : Screen("progress")
    object DailyChallenge : Screen("daily_challenge")
    object Settings : Screen("settings")
}
