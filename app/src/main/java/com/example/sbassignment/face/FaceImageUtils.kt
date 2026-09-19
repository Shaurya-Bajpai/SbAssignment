package com.example.sbassignment.face

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

object FaceImageUtils {
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val plane = imageProxy.planes.firstOrNull() ?: return null
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null

        // Apply CameraX rotation first.
        val rotatedBitmap = if (imageProxy.imageInfo.rotationDegrees != 0) {
            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }

        // Mirror because we are using the front camera.
        val mirroredBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.width, rotatedBitmap.height,
            Matrix().apply {
                postScale(-1f, 1f)
            },
            true
        )

        if (rotatedBitmap !== bitmap) bitmap.recycle()
        if (mirroredBitmap !== rotatedBitmap) rotatedBitmap.recycle()
        return mirroredBitmap
    }
}