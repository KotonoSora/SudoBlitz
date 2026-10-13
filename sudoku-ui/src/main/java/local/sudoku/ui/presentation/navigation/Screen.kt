package local.sudoku.ui.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("game")
    object Result : Screen("result")
    object CoinShop : Screen("coin_shop")
    object BoostSelection : Screen("boost_selection")
    object Progress : Screen("progress")
    object DailyChallenge : Screen("daily_challenge")
    object Settings : Screen("settings")
}
