package com.example.sbassignment.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbassignment.data.StaffEntity

val staffList = listOf(
    StaffEntity("1", "John Doe", "face_embedding_1", "face_image_path_1"),
    StaffEntity("2", "Jane Smith", "face_embedding_2", "face_image_path_2"),
    StaffEntity("3", "Bob Johnson", "face_embedding_3", "face_image_path_3"),
    StaffEntity("4", "Alice Brown", "face_embedding_4", "face_image_path_4"),
    StaffEntity("5", "Charlie Wilson", "face_embedding_5", "face_image_path_5"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffListScreen() {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Staff") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
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
            LazyColumn() {
                items(staffList) { staff ->
                    CardItem(staff)
                }
            }
        }
    }
}

@Composable
fun CardItem(staffMember: StaffEntity) {
    ElevatedCard(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = staffMember.name, fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaffListScreenPreview() {
    StaffListScreen()
}