package com.example.studyhub

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val courseName: String,
    val dueDate: String = "",
    val isCompleted: Boolean = false
)