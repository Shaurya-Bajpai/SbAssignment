package com.example.sbassignment.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbassignment.R
import com.example.sbassignment.data.FaceCaptureResult
import com.example.sbassignment.screens.components.OutlinedTextFieldItem

@Composable
fun AddStaffScreen(
    name: String,
    empId: String,
    onNameChange: (String) -> Unit,
    onEmpIdChange: (String) -> Unit,
    capturedResult: FaceCaptureResult?,
    onCaptureFace: () -> Unit,
    onAddDetails: (name: String, empId: String, capturedImage: FaceCaptureResult) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    val capturedImage = capturedResult?.image

    BackHandler {
        onBack()
        onReset()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Add Staff",
            color = Color.Black,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextFieldItem(
            value = name,
            onValueChange = { onNameChange(it) },
            placeholder = "Name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextFieldItem(
            value = empId,
            onValueChange = { onEmpIdChange(it) },
            placeholder = "Employee Id"
        )

        Spacer(modifier = Modifier.height(16.dp))

        /*
         * Camera / Selfie Card
         */
        ElevatedCard(
            modifier = Modifier
                .width(200.dp)
                .height(250.dp)
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            onClick = onCaptureFace
        ) {
            if(capturedImage != null) {
                Image(
                    bitmap = capturedImage.asImageBitmap(),
                    contentDescription = "Captured staff selfie",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(painter = painterResource(R.drawable.outline_camera_alt_24), contentDescription = "Camera", tint = Color.Black, modifier = Modifier.size(50.dp))

                    Spacer(Modifier.height(12.dp))

                    Text("Capture Selfie")
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                val result = capturedResult
                if (name.isNotBlank() && empId.isNotBlank() && result != null) {
                    onAddDetails(name.trim(), empId.trim(), result)
                }
            },
            enabled = name.isNotBlank() && empId.isNotBlank() && capturedResult != null,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonColors(
                containerColor = Color.Black,
                disabledContainerColor = Color.Gray,
                contentColor = Color.White,
                disabledContentColor = Color.Black,
            )
        ) {
            Text(text = "Add Details", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddStaffScreenPreview() {
    AddStaffScreen(
        name = "",
        empId = "",
        onNameChange = {},
        onEmpIdChange = {},
        capturedResult = null,
        onCaptureFace = {},
        onAddDetails = { _, _, _ -> },
        onBack = {},
        onReset = {}
    )
}