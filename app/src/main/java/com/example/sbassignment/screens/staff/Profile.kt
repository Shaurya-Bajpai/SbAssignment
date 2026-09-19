package com.example.sbassignment.screens.staff

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sbassignment.data.AttendanceEntity
import com.example.sbassignment.data.StaffEntity
import com.example.sbassignment.data.repository.AttendanceRepository
import com.example.sbassignment.data.repository.StaffRepository
import com.example.sbassignment.screens.components.SectionDivider
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
    var attendanceList by remember {
        mutableStateOf<List<AttendanceEntity>>(emptyList())
    }

    LaunchedEffect(employeeId) {
        withContext(Dispatchers.IO) {
            staff = staffRepository.getStaff(employeeId)
            attendanceList =
                attendanceRepository.getAttendanceForEmployeeList(employeeId)
        }
    }

    if (staff == null) {
        Text(
            text = "Loading...",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val currentStaff = staff!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color.Black),
                    RoundedCornerShape(16.dp)
                )
                .background(
                    Color.LightGray.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = File(currentStaff.faceImagePath),
                    contentDescription = "Profile photo",
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = currentStaff.name,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentStaff.employeeId,
                        fontSize = 16.sp
                    )
                }
            }
        }

        SectionDivider("Attendance")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(attendanceList) { attendance ->
                AttendanceCard(attendance)
            }
        }
    }
}

@Composable
fun AttendanceCard(
    attendance: AttendanceEntity
) {
    val date = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(Date(attendance.dateTime))

    val time = SimpleDateFormat(
        "hh:mm a",
        Locale.getDefault()
    ).format(Date(attendance.dateTime))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = File(attendance.selfiePath),
                    contentDescription = "Attendance selfie",
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Date: $date", fontSize = 16.sp)
                    Text("Time: $time", fontSize = 16.sp)
                    Text(
                        "Latitude: ${attendance.latitude}",
                        fontSize = 16.sp
                    )
                    Text(
                        "Longitude: ${attendance.longitude}",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}