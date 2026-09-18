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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbassignment.R
import com.example.sbassignment.screens.components.OutlinedTextFieldItem
import com.example.sbassignment.screens.components.SectionDivider

@Composable
fun StaffScreen() {
    var isAttendanceMarked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Staff",
            color = Color.Black,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(isAttendanceMarked){
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Camera", tint = Color.Black, modifier = Modifier.size(25.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAttendanceMarked) "Attendance Marked" else "Mark Attendance",
                        color = if (isAttendanceMarked) Color.Gray else Color.DarkGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
            }

            // Attendance Details after marking attendance of the day
            if (isAttendanceMarked) {
                SectionDivider("Details")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile Icon",
                                modifier = Modifier.size(100.dp),
                                tint = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "Name: ", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
                            Text(text = "Code: ", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                            Text(text = "Date: ", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                            Text(text = "Time: ", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                            Text(text = "Latitude: ", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                            Text(text = "Longitude: ", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                        }

                    }

                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaffScreenPreview() {
    StaffScreen()
}