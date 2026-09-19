package com.example.sbassignment.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sbassignment.data.AttendanceEntity

@Dao
interface AttendanceDao {

    @Insert
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance ORDER BY dateTime DESC")
    suspend fun getAllAttendance(): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId ORDER BY dateTime DESC")
    suspend fun getAttendanceForEmployee(employeeId: String): List<AttendanceEntity>
}