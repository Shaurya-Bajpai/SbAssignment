package com.example.sbassignment.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sbassignment.R

@Composable
fun FaceCameraScreen(staffId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if(!hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // CAMERA PREVIEW
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp,vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back"
                )
            }
            Text(text = "Face Verification", style = MaterialTheme.typography.titleLarge)
        }

        // INSTRUCTION
        Text(
            text = "Position your face inside the frame",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        // FACE FRAME
        Box(modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .border(width = 3.dp, color = Color.White, shape = RoundedCornerShape(24.dp))
        )

        // CAPTURE BUTTON
        FloatingActionButton(
            onClick = {
                // FaceNet capture will be added here
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_camera_alt_24),
                contentDescription = "Capture"
            )
        }
    }
}