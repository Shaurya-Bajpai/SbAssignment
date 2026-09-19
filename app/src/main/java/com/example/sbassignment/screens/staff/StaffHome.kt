package com.example.sbassignment.screens.staff

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
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

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (staff == null) {
        Text(
            text = "Loading...",
            modifier = Modifier.padding(24.dp)
        )
        return
    }

    val currentStaff = staff!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Admin") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            contentDescription = "Logout"
                        )
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // STAFF INFORMATION CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.Black),
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = File(currentStaff.faceImagePath),
                        contentDescription = "Staff photo",
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Column {

                        Text(
                            text = currentStaff.name,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Employee ID: ${currentStaff.employeeId}",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ATTENDANCE CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.Black),
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = "Latest Attendance",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (latestAttendance == null) {

                        Text(
                            text = "No attendance marked yet.",
                            fontSize = 15.sp
                        )

                    } else {

                        val date = SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                        ).format(
                            Date(latestAttendance!!.dateTime)
                        )

                        Text(
                            text = "Date & Time: $date",
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Status: Present",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LOCATION CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.Black),
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {

                        Text(
                            text = "Attendance Location",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (latestAttendance == null) {

                            Text(
                                text = "Not available",
                                fontSize = 14.sp
                            )

                        } else {
                            Text(
                                text = "Latitude: ${latestAttendance!!.latitude}",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Longitude: ${latestAttendance!!.longitude}",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MARK ATTENDANCE CARD
            Card(
                onClick = onMarkAttendance,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.Black),
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Attendance",
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = if (latestAttendance != null)
                            "Attendance Marked"
                        else
                            "Attendance Not Marked",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}