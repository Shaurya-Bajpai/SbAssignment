package com.example.sbassignment.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.example.sbassignment.R
import com.example.sbassignment.data.AttendanceEntity
import com.example.sbassignment.data.StaffEntity
import com.example.sbassignment.data.repository.AttendanceRepository
import com.example.sbassignment.data.repository.StaffRepository
import com.example.sbassignment.ui.theme.AppColors
import com.example.sbassignment.ui.theme.avatarColorFor
import com.example.sbassignment.ui.theme.initialsFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHomeScreen(
    employeeId: String,
    staffRepository: StaffRepository,
    attendanceRepository: AttendanceRepository,
    onMarkAttendance: () -> Unit,
    onLogout: () -> Unit
) {
    var staff by remember { mutableStateOf<StaffEntity?>(null) }
    var latestAttendance by remember { mutableStateOf<AttendanceEntity?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    DisposableEffect(employeeId, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        staff = staffRepository.getStaff(employeeId)
                        latestAttendance = attendanceRepository.getAttendanceForEmployee(employeeId)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", fontWeight = FontWeight.Bold, color = AppColors.OnPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(AppColors.HeaderGradient),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            contentDescription = "Logout",
                            tint = AppColors.OnPrimary
                        )
                    }
                }
            )
        },
        containerColor = AppColors.Background
    ) { padding ->
        val currentStaff = staff
        if (currentStaff == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Loading…", color = AppColors.OnSurfaceMuted)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero profile card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = AppColors.Primary.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    val file = File(currentStaff.faceImagePath)
                    if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = "Staff photo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, AppColors.Primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(avatarColorFor(currentStaff.name))
                                .border(2.dp, AppColors.Primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initialsFor(currentStaff.name), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(currentStaff.name, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = AppColors.OnSurface)
                        Text(
                            "ID: ${currentStaff.employeeId}",
                            fontSize = 13.sp,
                            color = AppColors.OnSurfaceMuted
                        )
                    }
                }
            }

            val present = latestAttendance != null
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(18.dp), spotColor = AppColors.OnSurface.copy(alpha = 0.06f)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (present) AppColors.Success else AppColors.Outline,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Latest Attendance",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.OnSurface,
                            modifier = Modifier.padding(start = 8.dp).weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (present) AppColors.SuccessBg else AppColors.Outline)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                if (present) "Present" else "Pending",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (present) AppColors.Success else AppColors.OnSurfaceMuted
                            )
                        }
                    }
                    if (!present) {
                        Text(
                            "No attendance marked yet",
                            fontSize = 14.sp,
                            color = AppColors.OnSurfaceMuted,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    } else {
                        val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                            .format(Date(latestAttendance!!.dateTime))
                        Text(date, fontSize = 14.sp, color = AppColors.OnSurface, modifier = Modifier.padding(top = 10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = AppColors.OnSurfaceMuted
                            )
                            Text(
                                "${latestAttendance!!.latitude}, ${latestAttendance!!.longitude}",
                                fontSize = 13.sp,
                                color = AppColors.OnSurfaceMuted,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onMarkAttendance,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = AppColors.Primary.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppColors.OnPrimary)
                Text(
                    text = if (present) "Attendance Marked" else "Mark Attendance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnPrimary,
                    modifier = Modifier.padding(start = 8.dp, top = 10.dp, bottom = 10.dp)
                )
            }
        }
    }
}