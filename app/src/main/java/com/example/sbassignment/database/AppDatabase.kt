package com.example.sbassignment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sbassignment.data.convertor.Converters
import com.example.sbassignment.dao.AttendanceDao
import com.example.sbassignment.dao.StaffDao
import com.example.sbassignment.data.entity.AttendanceEntity
import com.example.sbassignment.data.StaffEntity

@Database(entities = [StaffEntity::class, AttendanceEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun staffDao(): StaffDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}