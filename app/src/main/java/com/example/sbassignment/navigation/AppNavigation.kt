package com.example.sbassignment.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sbassignment.data.FaceCaptureMode
import com.example.sbassignment.data.FaceCaptureResult
import com.example.sbassignment.data.ImageStorage
import com.example.sbassignment.data.repository.FaceRecognitionRepository
import com.example.sbassignment.data.repository.StaffRepository
import com.example.sbassignment.data.repository.AttendanceRepository
import com.example.sbassignment.database.AppDatabase
import com.example.sbassignment.screens.LoginScreen
import com.example.sbassignment.screens.admin.AddStaffScreen
import com.example.sbassignment.screens.admin.AdminScreen
import com.example.sbassignment.screens.camera.FaceCameraScreen
import com.example.sbassignment.screens.staff.ProfileScreen
import com.example.sbassignment.screens.staff.StaffHomeScreen
import com.example.sbassignment.screens.staff.StaffScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var captureResult by remember { mutableStateOf<FaceCaptureResult?>(null) }
    var staffName by remember { mutableStateOf("") }
    var staffEmpId by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val staffRepository = remember { StaffRepository(AppDatabase.getInstance(context).staffDao()) }
    val attendanceRepository = remember { AttendanceRepository(AppDatabase.getInstance(context).attendanceDao()) }

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
                onStaffNavigate = {
                    coroutineScope.launch {
                        val staffList = withContext(Dispatchers.IO) { staffRepository.getAllStaff() }
                        if (staffList.isEmpty()) {
                            Toast.makeText(context, "No staff registered. Please contact admin.", Toast.LENGTH_SHORT).show()
                        } else {

                            navController.navigate(Screen.Staff.route)
                        }
                    }
                }
            )
        }

        // Admin Screen
        composable(Screen.Admin.route) {
            AdminScreen(
                onAddStaffButton = { navController.navigate(Screen.AddStaff.route) },
                staffRepository = staffRepository
            )
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

                    coroutineScope.launch {

                        val imagePath = withContext(Dispatchers.IO) {
                            ImageStorage.saveStaffImage(
                                context = context,
                                empId = empId,
                                bitmap = result.image
                            )
                        }

                        withContext(Dispatchers.IO) {
                            staffRepository.registerStaff(
                                empId = empId,
                                name = name,
                                embedding = result.embedding,
                                imagePath = imagePath
                            )

                            // Read it back from Room
                            val savedStaff =
                                staffRepository.getStaff(empId)

                            Log.d(
                                "ROOM_TEST",
                                "========== STAFF SAVED =========="
                            )

                            Log.d(
                                "ROOM_TEST",
                                "Employee ID = ${savedStaff?.employeeId}"
                            )

                            Log.d(
                                "ROOM_TEST",
                                "Name = ${savedStaff?.name}"
                            )

                            Log.d(
                                "ROOM_TEST",
                                "Image path = ${savedStaff?.faceImagePath}"
                            )

                            Log.d(
                                "ROOM_TEST",
                                "================================="
                            )
                        }

                        Toast.makeText(
                            context,
                            "Staff registered successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        resetAddStaffForm()
                        navController.popBackStack()
                    }
                },
                onReset = { resetAddStaffForm() },
                onBack = { navController.popBackStack() }
            )
        }

        // Staff Screen
        composable(Screen.Staff.route) {
            StaffScreen(
                staffRepository = staffRepository,
                onStaffSelected = { employeeId ->
                    navController.navigate("staff_home/$employeeId")
                }
            )
        }

        composable("staff_home/{employeeId}") { backStackEntry ->

            val employeeId =
                backStackEntry.arguments
                    ?.getString("employeeId")
                    .orEmpty()

            StaffHomeScreen(
                employeeId = employeeId,
                staffRepository = staffRepository,
                onMarkAttendance = {
                    navController.navigate("face_camera/$employeeId")
                }
            )
        }

        // STAFF ATTENDANCE CAMERA
        composable("face_camera/{employeeId}") { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId").orEmpty()

            FaceCameraScreen(
                mode = FaceCaptureMode.ATTENDANCE,
                onCaptureResult = { result ->
                    coroutineScope.launch {
                        val staff = withContext(Dispatchers.IO) {
                            staffRepository.getStaff(employeeId)
                        }

                        if (staff == null) {
                            Toast.makeText(context, "Staff not found", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val matcher = FaceRecognitionRepository()
                        val isMatch = matcher.isMatch(capturedEmbedding = result.embedding, registeredEmbedding = staff.faceEmbedding)

                        if (!isMatch) {
                            Toast.makeText(context, "Face does not match", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        // Face matched → save selfie
                        val selfiePath = withContext(Dispatchers.IO) {
                            ImageStorage.saveStaffImage(
                                context = context,
                                empId = "${employeeId}_${System.currentTimeMillis()}",
                                bitmap = result.image
                            )
                        }

                        // Save attendance
                        withContext(Dispatchers.IO) {
                            attendanceRepository.markAttendance(
                                employeeId = staff.employeeId,
                                name = staff.name,
                                selfiePath = selfiePath,
                                dateTime = System.currentTimeMillis()
                            )
                        }

                        Toast.makeText(context, "Attendance marked successfully", Toast.LENGTH_SHORT).show()
                        Log.d("ATTENDANCE", "Attendance saved for ${staff.employeeId}")

                        navController.popBackStack()
                    }
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