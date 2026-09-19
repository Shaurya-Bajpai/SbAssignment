package com.example.sbassignment.face

import android.graphics.Bitmap
import android.graphics.Rect
import kotlin.math.max
import kotlin.math.min

object FaceCropper {
    fun cropFace(bitmap: Bitmap, boundingBox: Rect): Bitmap? {
        val paddingX = (boundingBox.width() * 0.20f).toInt()
        val paddingY = (boundingBox.height() * 0.20f).toInt()
        val left = max(0, boundingBox.left - paddingX)
        val top = max(0, boundingBox.top - paddingY)
        val right = min(bitmap.width, boundingBox.right + paddingX)
        val bottom = min(bitmap.height, boundingBox.bottom + paddingY)
        val width = right - left
        val height = bottom - top
        if (width <= 0 || height <= 0) return null
        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }
}