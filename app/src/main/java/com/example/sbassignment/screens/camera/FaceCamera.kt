package com.example.sbassignment.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sbassignment.R
import com.example.sbassignment.data.FaceCaptureMode
import com.example.sbassignment.data.FaceCaptureResult
import com.example.sbassignment.face.FaceCropper
import com.example.sbassignment.face.FaceImageUtils
import com.example.sbassignment.face.FaceNetModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.sqrt

// ---- Design tokens ---------------------------------------------------------
private val FrameSize = 280.dp
private val FrameRadius = 32.dp
private val ScrimColor = Color(0xB3000000)          // 70% black outside the frame
private val FrameIdle = Color(0xCCFFFFFF)
private val FrameLocked = Color(0xFF4ADE80)         // face is inside the frame
private val StatusWarn = Color(0xFFFBBF24)
private val OnCamera = Color.White

@OptIn(ExperimentalGetImage::class)
@Composable
fun FaceCameraScreen(
    mode: FaceCaptureMode,
    onCaptureResult: (FaceCaptureResult) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current

    // ---- Face detection / model -------------------------------------------
    val detectorOptions = remember {
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setMinFaceSize(0.25f)
            .build()
    }
    val faceDetector = remember { FaceDetection.getClient(detectorOptions) }
    val faceNetModel = remember { FaceNetModel(context) }

    // Camera controller (selector is set once, not on every recomposition)
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    var faceDetected by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var embeddingGenerated by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            faceDetector.close()
            faceNetModel.close()
            cameraController.unbind()
        }
    }

    // ---- Camera permission ------------------------------------------------
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        PermissionRationale(
            onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onBack = onBack
        )
        return
    }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    // ---- Capture pipeline (unchanged logic, tidied) -----------------------
    fun postToMain(block: () -> Unit) =
        ContextCompat.getMainExecutor(context).execute(block)

    fun capture() {
        if (isProcessing) return
        if (!faceDetected) {
            Toast.makeText(context, "Position your face inside the frame first", Toast.LENGTH_SHORT).show()
            return
        }

        isProcessing = true
        embeddingGenerated = false

        cameraController.takePicture(
            cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        val bitmap = FaceImageUtils.imageProxyToBitmap(image)
                        if (bitmap == null) {
                            postToMain {
                                isProcessing = false
                                Toast.makeText(context, "Could not process captured image", Toast.LENGTH_SHORT).show()
                            }
                            return
                        }

                        // Run ML Kit on the SAME bitmap that we crop.
                        val inputImage = InputImage.fromBitmap(bitmap, 0)

                        faceDetector.process(inputImage)
                            .addOnSuccessListener(cameraExecutor) { faces ->
                                val face = faces.firstOrNull()
                                if (face == null) {
                                    postToMain {
                                        isProcessing = false
                                        Toast.makeText(context, "No face found in captured image", Toast.LENGTH_SHORT).show()
                                    }
                                    bitmap.recycle()
                                    return@addOnSuccessListener
                                }

                                val croppedFace = FaceCropper.cropFace(bitmap, face.boundingBox)
                                if (croppedFace == null) {
                                    postToMain {
                                        isProcessing = false
                                        Toast.makeText(context, "Could not crop face", Toast.LENGTH_SHORT).show()
                                    }
                                    bitmap.recycle()
                                    return@addOnSuccessListener
                                }

                                val embedding = faceNetModel.getEmbedding(croppedFace)
                                croppedFace.recycle()

                                var norm = 0f
                                for (v in embedding) norm += v * v
                                Log.d("FACE_RECOGNITION", "Embedding size=${embedding.size}, L2=${sqrt(norm)}")

                                val result = FaceCaptureResult(image = bitmap, embedding = embedding)
                                postToMain {
                                    isProcessing = false
                                    embeddingGenerated = true
                                    onCaptureResult(result)
                                }
                            }
                            .addOnFailureListener(cameraExecutor) { e ->
                                Log.e("FACE_RECOGNITION", "ML Kit capture detection failed", e)
                                bitmap.recycle()
                                postToMain {
                                    isProcessing = false
                                    Toast.makeText(context, "Face processing failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } catch (e: Exception) {
                        Log.e("FACE_RECOGNITION", "Face recognition pipeline failed", e)
                        postToMain {
                            isProcessing = false
                            Toast.makeText(context, "Face processing error: ${e.javaClass.simpleName}", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        // ImageCapture requires us to close the ImageProxy.
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("FACE_RECOGNITION", "Image capture failed", exception)
                    postToMain {
                        isProcessing = false
                        Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // ---- UI ---------------------------------------------------------------
    val frameColor by animateColorAsState(
        targetValue = if (faceDetected) FrameLocked else FrameIdle,
        animationSpec = tween(250),
        label = "frameColor"
    )
    val frameStroke by animateDpAsState(
        targetValue = if (faceDetected) 4.dp else 2.dp,
        animationSpec = tween(250),
        label = "frameStroke"
    )

    val title = when (mode) {
        FaceCaptureMode.REGISTER -> "Register face"
        else -> "Verify face"
    }
    val statusText = when {
        isProcessing -> "Processing face…"
        embeddingGenerated -> "Face captured"
        faceDetected -> "Looks good. Tap to capture"
        else -> "Center your face in the frame"
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // 1. Camera preview
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                cameraController.setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
                )
                cameraController.setImageAnalysisBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )

                val mlKitAnalyzer = MlKitAnalyzer(
                    listOf(faceDetector),
                    CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                    ContextCompat.getMainExecutor(ctx)
                ) { result ->
                    val face = result?.getValue(faceDetector)?.firstOrNull()
                    if (face == null) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    // Face must be roughly frontal
                    val frontal = abs(face.headEulerAngleY) <= 20f &&
                            abs(face.headEulerAngleZ) <= 15f
                    if (!frontal) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    val frameSizePx = with(density) { FrameSize.toPx() }
                    val previewWidth = previewView.width.toFloat()
                    val previewHeight = previewView.height.toFloat()
                    if (previewWidth <= 0f || previewHeight <= 0f) {
                        faceDetected = false
                        return@MlKitAnalyzer
                    }

                    // Small tolerance so the check isn't pixel-strict
                    val margin = frameSizePx * 0.08f
                    val frameLeft = (previewWidth - frameSizePx) / 2f - margin
                    val frameTop = (previewHeight - frameSizePx) / 2f - margin
                    val frameRight = frameLeft + frameSizePx + margin * 2
                    val frameBottom = frameTop + frameSizePx + margin * 2

                    val r = face.boundingBox
                    faceDetected = r.left >= frameLeft && r.top >= frameTop &&
                            r.right <= frameRight && r.bottom <= frameBottom
                }

                cameraController.setImageAnalysisAnalyzer(cameraExecutor, mlKitAnalyzer)
                previewView.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
                previewView
            }
        )

        // 2. Dimmed scrim with a rounded cut-out + animated frame outline
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frame = FrameSize.toPx()
            val radius = FrameRadius.toPx()
            val left = (size.width - frame) / 2f
            val top = (size.height - frame) / 2f

            val cutout = Path().apply {
                addRoundRect(
                    RoundRect(left, top, left + frame, top + frame, CornerRadius(radius))
                )
            }
            val full = Path().apply { addRect(Rect(Offset.Zero, size)) }
            drawPath(
                path = Path.combine(PathOperation.Difference, full, cutout),
                color = ScrimColor
            )
            drawRoundRect(
                color = frameColor,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(frame, frame),
                cornerRadius = CornerRadius(radius),
                style = Stroke(width = frameStroke.toPx())
            )
        }

        // 3. Top bar over a soft gradient
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color(0x99000000), Color.Transparent))
                )
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x55000000))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = OnCamera
                )
            }
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = OnCamera
            )
        }

        // 4. Status pill, sits just below the frame
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = FrameSize / 2 + 36.dp)
                .clip(CircleShape)
                .background(Color(0x99000000))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (faceDetected || embeddingGenerated) FrameLocked else StatusWarn)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = OnCamera
            )
        }

        // 5. Bottom gradient + shutter
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0xAA000000)))
                )
                .navigationBarsPadding()
                .padding(top = 40.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShutterButton(
                processing = isProcessing,
                ready = faceDetected,
                onClick = ::capture
            )
        }
    }
}

@Composable
private fun ShutterButton(
    processing: Boolean,
    ready: Boolean,
    onClick: () -> Unit
) {
    val ringColor by animateColorAsState(
        targetValue = if (ready) FrameLocked else Color(0x99FFFFFF),
        animationSpec = tween(250),
        label = "ringColor"
    )
    val fillAlpha = if (ready || processing) 1f else 0.6f

    Box(
        modifier = Modifier
            .size(84.dp)
            .border(4.dp, ringColor, CircleShape)
            .padding(7.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = fillAlpha))
            .clickable(enabled = !processing, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (processing) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color.Black,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.outline_camera_alt_24),
                contentDescription = "Capture",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun PermissionRationale(
    onRequest: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera access needed",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Allow camera access so we can verify your face.",
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRequest, shape = RoundedCornerShape(16.dp)) {
            Text("Allow camera")
        }
        Text(
            text = "Go back",
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable(onClick = onBack)
                .padding(8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}