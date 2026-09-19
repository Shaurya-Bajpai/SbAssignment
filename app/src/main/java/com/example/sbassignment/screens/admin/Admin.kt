package com.example.sbassignment.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import com.example.sbassignment.data.StaffEntity
import com.example.sbassignment.data.repository.StaffRepository
import com.example.sbassignment.ui.theme.AppColors
import com.example.sbassignment.ui.theme.avatarColorFor
import com.example.sbassignment.ui.theme.initialsFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onAddStaffButton: () -> Unit = {},
    staffRepository: StaffRepository,
    onStaffSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var staffList by remember { mutableStateOf<List<StaffEntity>>(emptyList()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    staffList = withContext(Dispatchers.IO) { staffRepository.getAllStaff() }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.OnPrimary) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStaffButton,
                containerColor = AppColors.Primary,
                contentColor = AppColors.OnPrimary,
                shape = CircleShape,
                modifier = Modifier.shadow(14.dp, CircleShape, spotColor = AppColors.Primary.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add staff")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = AppColors.Background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Hero stat card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = AppColors.Primary.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppColors.PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(R.drawable.outline_group_24), contentDescription = null, tint = AppColors.Primary)
                    }
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            "${staffList.size}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
                        )
                        Text(
                            "Staff registered",
                            fontSize = 13.sp,
                            color = AppColors.OnSurfaceMuted
                        )
                    }
                }
            }

            if (staffList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = AppColors.Outline
                        )
                        Text(
                            "No staff yet — tap + to add one",
                            color = AppColors.OnSurfaceMuted,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = staffList, key = { it.employeeId }) { staff ->
                        CardItem(staffMember = staff, onClick = { onStaffSelected(staff.employeeId) })
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(staffMember: StaffEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = AppColors.OnSurface.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val file = File(staffMember.faceImagePath)
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
                        .background(avatarColorFor(staffMember.name)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initialsFor(staffMember.name), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(staffMember.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.OnSurface)
                Text(
                    "ID: ${staffMember.employeeId}",
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