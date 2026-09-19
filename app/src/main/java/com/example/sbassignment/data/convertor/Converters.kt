package com.example.sbassignment.data.convertor

import androidx.room.TypeConverter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Converters {
    @TypeConverter
    fun fromFloatArray(array: FloatArray): ByteArray {
        val buffer = ByteBuffer
            .allocate(array.size * Float.SIZE_BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
        array.forEach {
            buffer.putFloat(it)
        }
        return buffer.array()
    }

    @TypeConverter
    fun toFloatArray(bytes: ByteArray): FloatArray {
        val buffer = ByteBuffer
            .wrap(bytes)
            .order(ByteOrder.LITTLE_ENDIAN)
        return FloatArray(bytes.size / Float.SIZE_BYTES) {
            buffer.float
        }
    }
}