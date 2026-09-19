package com.example.sbassignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: String,
    val name: String,
    val faceEmbedding: FloatArray,
    val faceImagePath: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StaffEntity

        if (id != other.id) return false
        if (employeeId != other.employeeId) return false
        if (name != other.name) return false
        if (!faceEmbedding.contentEquals(other.faceEmbedding)) return false
        if (faceImagePath != other.faceImagePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + employeeId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + faceEmbedding.contentHashCode()
        result = 31 * result + (faceImagePath?.hashCode() ?: 0)
        return result
    }
}