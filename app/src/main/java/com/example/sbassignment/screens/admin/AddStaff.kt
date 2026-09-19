package com.example.sbassignment.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbassignment.R
import com.example.sbassignment.data.FaceCaptureResult
import com.example.sbassignment.screens.components.OutlinedTextFieldItem
import com.example.sbassignment.ui.theme.AppColors

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
            .background(AppColors.Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Staff",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Capture a selfie and fill in the details",
            fontSize = 13.sp,
            color = AppColors.OnSurfaceMuted,
            modifier = Modifier.padding(bottom = 22.dp)
        )

        Card(
            modifier = Modifier
                .size(168.dp)
                .clip(RoundedCornerShape(24.dp))
                .shadow(14.dp, RoundedCornerShape(24.dp), spotColor = AppColors.Primary.copy(alpha = 0.3f)),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            onClick = onCaptureFace
        ) {
            if (capturedImage != null) {
                Image(
                    bitmap = capturedImage.asImageBitmap(),
                    contentDescription = "Captured staff selfie",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().background(AppColors.PrimarySoft),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_camera_alt_24),
                        contentDescription = "Camera",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(42.dp)
                    )
                    Text(
                        "Capture Selfie",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Primary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = AppColors.OnSurface.copy(alpha = 0.06f)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextFieldItem(value = name, onValueChange = onNameChange, placeholder = "Name")
                OutlinedTextFieldItem(value = empId, onValueChange = onEmpIdChange, placeholder = "Employee Id")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val result = capturedResult
                if (name.isNotBlank() && empId.isNotBlank() && result != null) {
                    onAddDetails(name.trim(), empId.trim(), result)
                }
            },
            enabled = name.isNotBlank() && empId.isNotBlank() && capturedResult != null,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = AppColors.Primary.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                disabledContainerColor = AppColors.Outline
            )
        ) {
            Text(
                text = "Add Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 10.dp)
            )
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