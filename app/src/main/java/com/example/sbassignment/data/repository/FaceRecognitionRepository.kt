package com.example.sbassignment.data.repository

class FaceRecognitionRepository {
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size)
        var sum = 0f
        for (i in a.indices) {
            sum += a[i] * b[i]
        }
        return sum
    }

    fun isMatch(capturedEmbedding: FloatArray, registeredEmbedding: FloatArray, threshold: Float = 0.65f): Boolean {
        val similarity = cosineSimilarity(capturedEmbedding, registeredEmbedding)
        return similarity >= threshold
    }
}