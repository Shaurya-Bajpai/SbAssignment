package com.example.sbassignment.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbassignment.screens.components.OutlinedTextFieldItem
import com.example.sbassignment.ui.theme.AppColors

@Composable
fun LoginScreen(onAdminNavigate: () -> Unit = {}, onStaffNavigate: () -> Unit = {}) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.HeaderGradient)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(72.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(AppColors.PrimaryGradient),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = AppColors.OnPrimary)
            }
        }

        Text(
            "Welcome Back",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnPrimary,
            modifier = Modifier.padding(top = 18.dp)
        )
        Text(
            "Sign in to continue",
            fontSize = 14.sp,
            color = AppColors.OnPrimary.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = AppColors.PrimaryDark.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextFieldItem(value = email, onValueChange = { email = it }, placeholder = "Email")
                OutlinedTextFieldItem(value = password, onValueChange = { password = it }, placeholder = "Password")

                Text(
                    "Forgot Password?",
                    color = AppColors.Primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.End)
                )

                Button(
                    onClick = {
                        when {
                            email == "admin" && password == "admin" -> onAdminNavigate()
                            email == "staff" && password == "staff" -> onStaffNavigate()
                            email.isEmpty() || password.isEmpty() ->
                                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = AppColors.Primary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text(
                        "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}