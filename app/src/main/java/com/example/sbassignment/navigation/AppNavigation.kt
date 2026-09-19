package com.example.sbassignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sbassignment.data.FaceCaptureMode
import com.example.sbassignment.data.FaceCaptureResult
import com.example.sbassignment.screens.LoginScreen
import com.example.sbassignment.screens.admin.AddStaffScreen
import com.example.sbassignment.screens.admin.AdminScreen
import com.example.sbassignment.screens.camera.FaceCameraScreen
import com.example.sbassignment.screens.staff.ProfileScreen
import com.example.sbassignment.screens.staff.StaffScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var captureResult by remember { mutableStateOf<FaceCaptureResult?>(null) }
    var staffName by remember { mutableStateOf("") }
    var staffEmpId by remember { mutableStateOf("") }
    fun resetAddStaffForm() {
        staffName = ""
        staffEmpId = ""
        captureResult = null
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onAdminNavigate = { navController.navigate(Screen.Admin.route) },
                onStaffNavigate = { navController.navigate(Screen.Staff.route) }
            )
        }

        // Admin Screen
        composable(Screen.Admin.route) {
            AdminScreen(onAddStaffButton = { navController.navigate(Screen.AddStaff.route) })
        }

        // Admin Screen
        composable(Screen.AddStaff.route) {
            AddStaffScreen(
                name = staffName,
                empId = staffEmpId,
                onNameChange = { staffName = it },
                onEmpIdChange = { staffEmpId = it },
                capturedResult = captureResult,
                onCaptureFace = { navController.navigate("register_camera") },
                onAddDetails = { name, empId, result ->
                    // Room saving will be added next
                },
                onReset = { resetAddStaffForm() }
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

        // STAFF ATTENDANCE CAMERA
        composable("face_camera/{staffId}") { backStackEntry ->
            val staffId = backStackEntry.arguments?.getString("staffId").orEmpty()

            FaceCameraScreen(
                mode = FaceCaptureMode.ATTENDANCE,
                onCaptureResult = { result ->
                    // Attendance matching will be added later
                },
                onBack = { navController.popBackStack() }
            )
        }
        // ADMIN REGISTRATION CAMERA
        composable("register_camera") {
            FaceCameraScreen(
                mode = FaceCaptureMode.REGISTER,
                onCaptureResult = { result ->
                    captureResult = result
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) { ProfileScreen() }
    }

}