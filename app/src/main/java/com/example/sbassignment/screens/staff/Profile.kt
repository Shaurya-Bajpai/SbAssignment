package com.example.sbassignment.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sbassignment.data.AttendanceEntity
import com.example.sbassignment.data.StaffEntity
import com.example.sbassignment.data.repository.AttendanceRepository
import com.example.sbassignment.data.repository.StaffRepository
import com.example.sbassignment.ui.theme.AppColors
import com.example.sbassignment.ui.theme.avatarColorFor
import com.example.sbassignment.ui.theme.initialsFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    employeeId: String,
    staffRepository: StaffRepository,
    attendanceRepository: AttendanceRepository
) {
    var staff by remember { mutableStateOf<StaffEntity?>(null) }
    var attendanceList by remember { mutableStateOf<List<AttendanceEntity>>(emptyList()) }

    LaunchedEffect(employeeId) {
        withContext(Dispatchers.IO) {
            staff = staffRepository.getStaff(employeeId)
            attendanceList = attendanceRepository.getAttendanceForEmployeeList(employeeId)
        }
    }

    val currentStaff = staff
    if (currentStaff == null) {
        Text("Loading…", modifier = Modifier.padding(16.dp), color = AppColors.OnSurfaceMuted)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Gradient hero header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.HeaderGradient)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val file = File(currentStaff.faceImagePath)
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Profile photo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(avatarColorFor(currentStaff.name)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initialsFor(currentStaff.name), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(currentStaff.name, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = AppColors.OnPrimary)
                    Text(
                        "ID: ${currentStaff.employeeId}",
                        fontSize = 13.sp,
                        color = AppColors.OnPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                "Attendance",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "${attendanceList.size} record${if (attendanceList.size == 1) "" else "s"}",
                fontSize = 12.sp,
                color = AppColors.OnSurfaceMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (attendanceList.isEmpty()) {
                Text(
                    "No attendance records",
                    fontSize = 14.sp,
                    color = AppColors.OnSurfaceMuted
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(attendanceList) { attendance -> AttendanceCard(attendance) }
                }
            }
        }
    }
}

@Composable
fun AttendanceCard(attendance: AttendanceEntity) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(attendance.dateTime))
    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(attendance.dateTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = AppColors.OnSurface.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(AppColors.Primary)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val file = File(attendance.selfiePath)
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Attendance selfie",
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AppColors.PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = AppColors.Primary, fontWeight = FontWeight.Bold)
                    }
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("$date · $time", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.OnSurface)
                    Text(
                        "${attendance.latitude}, ${attendance.longitude}",
                        fontSize = 12.sp,
                        color = AppColors.OnSurfaceMuted
                    )
                }
            }
        }
    }
}