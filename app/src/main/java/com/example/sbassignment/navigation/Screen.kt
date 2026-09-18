package com.example.sbassignment.navigation

open class Screen(val route: String) {
    object Login: Screen("login")
    object Home: Screen("home")
    object Profile: Screen("profile")
    object Admin: Screen("admin")
    object Staff: Screen("staff")
}
