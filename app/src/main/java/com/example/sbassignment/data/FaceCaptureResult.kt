package com.example.sbassignment.data

import android.graphics.Bitmap

data class FaceCaptureResult(
    val image: Bitmap,
    val embedding: FloatArray
)