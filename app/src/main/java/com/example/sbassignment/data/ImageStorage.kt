package com.example.sbassignment.data

import android.content.Context
import android.graphics.Bitmap
import java.io.File

object ImageStorage {
    fun saveStaffImage(context: Context, empId: String, bitmap: Bitmap): String {
        val directory = File(context.filesDir, "staff_faces")
        if (!directory.exists())  directory.mkdirs()

        val file = File(directory, "${empId}.jpg")
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }
        return file.absolutePath
    }
}