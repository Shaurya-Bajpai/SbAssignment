package com.example.sbassignment.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sbassignment.R
import com.example.sbassignment.face.FaceNetModel
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun FaceCameraScreen(staffId: String, onBack: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "FaceCameraScreen is running",
            Toast.LENGTH_LONG
        ).show()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current
    var faceDetected by remember { mutableStateOf(false) }

    Log.e(
        "CAMERA_SCREEN_TEST",
        "FaceCameraScreen is running"
    )
    val faceNetModel = remember {
        try {
            FaceNetModel(context)
        } catch (e: Exception) {

            Toast.makeText(
                context,
                "FaceNet ERROR: ${e.javaClass.simpleName}",
                Toast.LENGTH_LONG
            ).show()

            e.printStackTrace()

            null
        }
    }

//    DisposableEffect(Unit) {
//        onDispose {
//            faceNetModel.close()
//        }
//    }

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

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // CAMERA PREVIEW
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->

                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraController = LifecycleCameraController(ctx)
                cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                val detectorOptions = FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .setMinFaceSize(0.25f)
                        .build()

                val faceDetector = FaceDetection.getClient(detectorOptions)
                val mainExecutor = ContextCompat.getMainExecutor(ctx)
                val mlKitAnalyzer = MlKitAnalyzer(
                    listOf(faceDetector),
                    COORDINATE_SYSTEM_VIEW_REFERENCED,
                    mainExecutor
                ) { result ->

                    val faces = result?.getValue(faceDetector)
                    val face = faces?.firstOrNull()
                    if (face == null) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    // 1. FACE SIZE
                    val faceWidth = face.boundingBox.width()
                    val faceHeight = face.boundingBox.height()
                    val minimumFaceSize = 150
                    if (faceWidth < minimumFaceSize ||faceHeight < minimumFaceSize) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    // 2. FACE MUST BE REASONABLY FRONTAL
                    val frontal = kotlin.math.abs(face.headEulerAngleY) <= 20f &&
                                kotlin.math.abs(face.headEulerAngleZ) <= 15f
                    if (!frontal) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    // 3. FRAME COORDINATES
                    val frameSizePx = with(density) { 280.dp.toPx() }
                    val previewWidth = previewView.width.toFloat()
                    val previewHeight = previewView.height.toFloat()

                    if (previewWidth <= 0f ||previewHeight <= 0f) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    val frameLeft = (previewWidth - frameSizePx) / 2f
                    val frameTop = (previewHeight - frameSizePx) / 2f
                    val frameRight = frameLeft + frameSizePx
                    val frameBottom = frameTop + frameSizePx

                    // 4. ML KIT BOUNDING BOX IS NOW ALREADY IN PREVIEWVIEW COORDINATES
                    val faceRect = face.boundingBox
                    val insideFrame =
                        faceRect.left >= frameLeft &&
                        faceRect.top >= frameTop &&
                        faceRect.right <= frameRight &&
                        faceRect.bottom <= frameBottom

                    faceDetected = insideFrame
                }

                cameraController.setImageAnalysisAnalyzer(cameraExecutor, mlKitAnalyzer)

                previewView.controller = cameraController

                cameraController.bindToLifecycle(lifecycleOwner)

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
            text = if (faceDetected) "Face detected" else "Position your face inside the frame",
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
                .border(
                    width = 3.dp,
                    color = if (faceDetected) Color.Green else Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
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
                painter = painterResource(
                    R.drawable.outline_camera_alt_24
                ),
                contentDescription = "Capture"
            )
        }
    }
}