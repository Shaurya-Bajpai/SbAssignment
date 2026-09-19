package com.example.sbassignment.face

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

class FaceNetModel(context: Context) {

    companion object {
        private const val TAG = "FaceNetModel"
        private const val MODEL_NAME = "facenet.tflite"
        private const val INPUT_SIZE = 160
        private const val EMBEDDING_SIZE = 512
    }

    private val interpreter: Interpreter

    init {
        val model = loadModelFile(context)
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }

        interpreter = Interpreter(model, options)
        interpreter.allocateTensors()
        Log.d(TAG, "FaceNet initialized")
        Log.d(TAG, "Input = ${interpreter.getInputTensor(0).shape().contentToString()}")
        Log.d(TAG, "Output = ${interpreter.getOutputTensor(0).shape().contentToString()}")
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(MODEL_NAME)
        FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
            val fileChannel = inputStream.channel
            return fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.startOffset,
                fileDescriptor.declaredLength
            )
        }
    }

    fun getEmbedding(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3).apply {
                order(ByteOrder.nativeOrder())
            }

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        resizedBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        for (pixel in pixels) {
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF

            /*
             * FaceNet preprocessing:
             *
             * [0, 255]
             *       ↓
             * [-1, 1]
             */
            inputBuffer.putFloat((red - 127.5f) / 127.5f)
            inputBuffer.putFloat((green - 127.5f) / 127.5f)
            inputBuffer.putFloat((blue - 127.5f) / 127.5f)
        }
        inputBuffer.rewind()
        val output = Array(1) { FloatArray(EMBEDDING_SIZE) }
        interpreter.run(inputBuffer, output)
        return l2Normalize(output[0])
    }

    private fun l2Normalize(embedding: FloatArray): FloatArray {
        var sum = 0f
        for (value in embedding) {
            sum += value * value
        }

        val norm = sqrt(sum)
        if (norm == 0f) return embedding

        return FloatArray(embedding.size) { index ->
            embedding[index] / norm
        }
    }

    fun close() {
        interpreter.close()
    }
}