package com.example.sbassignment.data.repository

import com.example.sbassignment.dao.StaffDao
import com.example.sbassignment.data.StaffEntity

class StaffRepository(private val staffDao: StaffDao) {
    suspend fun registerStaff(empId: String, name: String, embedding: FloatArray, imagePath: String) {
        val staff = StaffEntity(
            employeeId = empId,
            name = name,
            faceEmbedding = embedding,
            faceImagePath = imagePath
        )
        staffDao.insertStaff(staff)
    }

    suspend fun getStaff(empId: String): StaffEntity? {
        return staffDao.getStaff(empId)
    }

    suspend fun getAllStaff(): List<StaffEntity> {
        return staffDao.getAllStaff()
    }
}