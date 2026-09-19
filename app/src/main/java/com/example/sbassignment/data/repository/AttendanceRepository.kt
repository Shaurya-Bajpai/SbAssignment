package com.example.sbassignment.data.repository

import com.example.sbassignment.dao.AttendanceDao
import com.example.sbassignment.data.AttendanceEntity

class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    suspend fun markAttendance(
        employeeId: String,
        name: String,
        selfiePath: String,
        dateTime: Long,
        latitude: Double,
        longitude: Double
    ) {
        attendanceDao.insertAttendance(
            AttendanceEntity(
                employeeId = employeeId,
                name = name,
                selfiePath = selfiePath,
                dateTime = dateTime,
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    suspend fun getAllAttendance(): List<AttendanceEntity> {
        return attendanceDao.getAllAttendance()
    }

    suspend fun getAttendanceForEmployee(employeeId: String): AttendanceEntity? {
        return attendanceDao.getAttendanceForEmployee(employeeId)
    }

    suspend fun getAttendanceForEmployeeList(employeeId: String): List<AttendanceEntity> {
        return attendanceDao.getAttendanceForEmployeeList(employeeId)
    }
}