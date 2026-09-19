package com.example.sbassignment.data.repository

import com.example.sbassignment.dao.AttendanceDao
import com.example.sbassignment.data.AttendanceEntity

class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    suspend fun markAttendance(
        employeeId: String,
        name: String,
        selfiePath: String,
        dateTime: Long
    ) {
        attendanceDao.insertAttendance(
            AttendanceEntity(
                employeeId = employeeId,
                name = name,
                selfiePath = selfiePath,
                dateTime = dateTime,
                latitude = 0.0,
                longitude = 0.0
            )
        )
    }

    suspend fun getAllAttendance(): List<AttendanceEntity> {
        return attendanceDao.getAllAttendance()
    }

    suspend fun getAttendanceForEmployee(employeeId: String): AttendanceEntity? {
        return attendanceDao.getAttendanceForEmployee(employeeId)
    }
}