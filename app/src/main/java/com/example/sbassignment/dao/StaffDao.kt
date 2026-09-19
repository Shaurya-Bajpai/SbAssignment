package com.example.sbassignment.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sbassignment.data.StaffEntity

@Dao
interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity)

    @Query("SELECT * FROM staff WHERE empId = :empId LIMIT 1")
    suspend fun getStaff(empId: String): StaffEntity?

    @Query("SELECT * FROM staff")
    suspend fun getAllStaff(): List<StaffEntity>

    @Query("DELETE FROM staff WHERE empId = :empId")
    suspend fun deleteStaff(empId: String)
}