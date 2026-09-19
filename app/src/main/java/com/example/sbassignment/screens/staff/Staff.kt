package com.example.sbassignment.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sbassignment.R
import com.example.sbassignment.data.StaffEntity
import com.example.sbassignment.data.repository.StaffRepository
import com.example.sbassignment.ui.theme.AppColors
import com.example.sbassignment.ui.theme.avatarColorFor
import com.example.sbassignment.ui.theme.initialsFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun StaffScreen(
    staffRepository: StaffRepository,
    onStaffSelected: (String) -> Unit
) {
    var staffList by remember { mutableStateOf<List<StaffEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        staffList = withContext(Dispatchers.IO) { staffRepository.getAllStaff() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp)
    ) {
        Text(
            "Select Staff",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "Choose your profile to continue",
            fontSize = 13.sp,
            color = AppColors.OnSurfaceMuted,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (staffList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_people_outline_24),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = AppColors.Outline
                    )
                    Text(
                        "No staff registered",
                        fontSize = 15.sp,
                        color = AppColors.OnSurfaceMuted,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = staffList, key = { it.employeeId }) { staff ->
                    StaffCard(staff = staff, onClick = { onStaffSelected(staff.employeeId) })
                }
            }
        }
    }
}

@Composable
fun StaffCard(staff: StaffEntity, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = AppColors.OnSurface.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val file = File(staff.faceImagePath)
            if (file.exists()) {
                AsyncImage(
                    model = file,
                    contentDescription = "Staff face",
                    modifier = Modifier.size(52.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(avatarColorFor(staff.name)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initialsFor(staff.name), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(staff.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.OnSurface)
                Text(
                    "ID: ${staff.employeeId}",
                    fontSize = 13.sp,
                    color = AppColors.OnSurfaceMuted
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = AppColors.Outline
            )
        }
    }
}