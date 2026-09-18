package com.example.sbassignment.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbassignment.screens.components.OutlinedTextFieldItem

@Composable
fun LoginScreen(onAdminNavigate: () -> Unit = {}, onStaffNavigate: () -> Unit = {}) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(16.dp))
                .background(Color.LightGray.copy(0.3f), RoundedCornerShape(16.dp))
                .padding(top = 20.dp, bottom = 30.dp, start = 16.dp, end = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Login",
                color = Color.Black,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextFieldItem(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextFieldItem(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = "Forgot Password?",
                color = Color.Blue,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .align(Alignment.End)
            )

            Button(
                onClick = {
                    if(email == "admin" && password == "admin") {
                        onAdminNavigate()
                    } else if(email == "staff" && password == "staff") {
                        onStaffNavigate()
                    } else if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
                    }
                },
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
                Text(text = "Login", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}