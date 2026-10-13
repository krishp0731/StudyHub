package com.example.studyhub

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AssignmentEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
}