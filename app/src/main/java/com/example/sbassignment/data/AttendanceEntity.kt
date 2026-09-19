package com.example.sbassignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeId: String,
    val name: String,
    val selfiePath: String,
    val dateTime: Long,
    val latitude: Double,
    val longitude: Double
)