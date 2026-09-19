package com.example.sbassignment.face

import android.content.Context
import android.util.Base64
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FaceEmbeddingStore(context: Context) {

    companion object {
        private const val PREF_NAME = "face_embeddings"
        private const val KEY_PREFIX = "embedding_"
        private const val EMBEDDING_SIZE = 512
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveEmbedding(staffId: String, embedding: FloatArray) {
        require(embedding.size == EMBEDDING_SIZE) {
            "Expected $EMBEDDING_SIZE values, got ${embedding.size}"
        }

        val byteBuffer = ByteBuffer
                .allocate(embedding.size * Float.SIZE_BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)

        embedding.forEach {
            byteBuffer.putFloat(it)
        }

        val encoded = Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP)

        preferences.edit().putString(KEY_PREFIX + staffId, encoded).apply()
    }

    fun getEmbedding(staffId: String): FloatArray? {
        val encoded = preferences.getString(KEY_PREFIX + staffId, null) ?: return null
        val bytes =
            try {
                Base64.decode(encoded, Base64.NO_WRAP)
            } catch (e: IllegalArgumentException) {
                return null
            }

        if (bytes.size != EMBEDDING_SIZE * Float.SIZE_BYTES) {
            return null
        }

        val byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        return FloatArray(EMBEDDING_SIZE) {
            byteBuffer.float
        }
    }

    fun hasEmbedding(staffId: String): Boolean {
        return preferences.contains(KEY_PREFIX + staffId)
    }

    fun deleteEmbedding(staffId: String) {
        preferences.edit().remove(KEY_PREFIX + staffId).apply()
    }
}