package com.example.studyhub

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Insert
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)


    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    @Query("SELECT * FROM assignments")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>
}