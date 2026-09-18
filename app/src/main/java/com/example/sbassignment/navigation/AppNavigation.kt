package com.example.sbassignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sbassignment.screens.LoginScreen
import com.example.sbassignment.screens.camera.FaceCameraScreen
import com.example.sbassignment.screens.staff.ProfileScreen
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
        composable(Screen.Staff.route) {
            StaffScreen(
                onStaffClick = { staffId ->
                    navController.navigate("face_camera/$staffId")
                }
            )
        }

        composable(Screen.Profile.route) { ProfileScreen() }

        composable("face_camera/{staffId}") { backStackEntry ->
            val staffId = backStackEntry.arguments?.getString("staffId")

            FaceCameraScreen(
                staffId = staffId.orEmpty(),
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }

}