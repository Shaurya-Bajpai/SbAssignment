package com.example.sbassignment.screens.admin

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.example.sbassignment.data.StaffEntity
import com.example.sbassignment.data.repository.StaffRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onAddStaffButton: () -> Unit = {},
    staffRepository: StaffRepository,
    onStaffSelected: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var staffList by remember { mutableStateOf<List<StaffEntity>>(emptyList()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    staffList = withContext(Dispatchers.IO) {
                        staffRepository.getAllStaff()
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Admin") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddStaffButton() },
                containerColor = Color.Black.copy(0.5f),
                shape = FloatingActionButtonDefaults.largeShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(36.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // List of Staff Members
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = staffList, key = { it.employeeId }) { staff ->
                    CardItem(
                        staffMember = staff,
                        onClick = {
                            onStaffSelected(staff.employeeId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardItem(staffMember: StaffEntity, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = File(staffMember.faceImagePath),
                contentDescription = "Staff face",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, Color.Black), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = staffMember.name, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Employee ID: ${staffMember.employeeId}", fontSize = 14.sp)
            }
        }
    }
}