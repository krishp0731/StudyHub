package com.example.studyhub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AssignmentViewModel(private val dao: AssignmentDao) : ViewModel() {

    // Streams real-time database updates sorted by completion status
    val assignments: StateFlow<List<AssignmentEntity>> = dao.getAllAssignments().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Inserts a new assignment with an optional due date
    fun addAssignment(title: String, courseName: String, dueDate: String) {
        if (title.isNotBlank() && courseName.isNotBlank()) {
            viewModelScope.launch {
                dao.insertAssignment(
                    AssignmentEntity(
                        title = title, courseName = courseName, dueDate = dueDate
                    )
                )
            }
        }
    }

    // Toggles the isCompleted boolean and updates SQLite via Room
    fun toggleAssignmentCompletion(assignment: AssignmentEntity) {
        viewModelScope.launch {
            dao.updateAssignment(
                assignment.copy(isCompleted = !assignment.isCompleted)
            )
        }
    }

    // Removes an assignment from SQLite
    fun deleteAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch {
            dao.deleteAssignment(assignment)
        }
    }
}