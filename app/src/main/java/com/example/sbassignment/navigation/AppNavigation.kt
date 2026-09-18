package com.example.sbassignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sbassignment.screens.LoginScreen
import com.example.sbassignment.screens.admin.ProfileScreen
import com.example.sbassignment.screens.staff.StaffScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onAdminNavigate = { navController.navigate(Screen.Admin.route) },
                onStaffNavigate = { navController.navigate(Screen.Staff.route) }
            )
        }

        // Staff Screen
        composable(Screen.Staff.route) { StaffScreen() }


//        composable(Screen.Admin.route) { AdminScreen(onNavigate = { navController.navigate("profile") }) }
        composable("profile") { ProfileScreen() }
    }

}