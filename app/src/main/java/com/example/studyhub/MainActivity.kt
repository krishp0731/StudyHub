package com.example.studyhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.studyhub.ui.theme.StudyHubTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "assignment_database"
        ).build()

        val viewModel = AssignmentViewModel(db.assignmentDao())
        setContent {
            StudyHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AssignmentHubScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentHubScreen(
    viewModel: AssignmentViewModel, modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf("All") } // 1. Added missing state

    val assignmentList by viewModel.assignments.collectAsState()

    val avaiableCourses = remember(assignmentList) {
        listOf("All") + assignmentList
            .filter { !it.isCompleted }
            .map { it.courseName }
            .distinct()
            .filter { it.isNotBlank() }
    }
    LaunchedEffect(avaiableCourses) {
        if (selectedCourse != "All" && selectedCourse !in avaiableCourses) {
            selectedCourse = "All"
        }
    }

    val filteredAssignments = remember(assignmentList, selectedCourse) {
        assignmentList.filter { assignment ->
            val isNotCompleted = !assignment.isCompleted
            val matchesCourse = selectedCourse == "All" || assignment.courseName.equals(
                selectedCourse,
                ignoreCase = true
            )

            isNotCompleted && matchesCourse
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("StudyHub") }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { showBottomSheet = true },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "Add Assignment"
            )
        }
    }) { innerPadding ->
        // 2. Wrap in Column to place chips above the list
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (assignmentList.isNotEmpty()) {
                CourseFilterChips(
                    courses = avaiableCourses,
                    selectedCourse = selectedCourse,
                    onCourseSelected = { selectedCourse = it })
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (filteredAssignments.isEmpty()) {
                    if (assignmentList.isEmpty()) {
                        EmptyState()
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No assignments found for $selectedCourse",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // 3. Pass filteredAssignments to AssignmentList
                    AssignmentList(
                        assignments = filteredAssignments,
                        onToggleComplete = { assignment ->
                            viewModel.toggleAssignmentCompletion(
                                assignment
                            )
                        },
                        onDeleteClick = { assignment -> viewModel.deleteAssignment(assignment) })
                }

                if (showBottomSheet) {
                    AddAssignmentBottomSheet(
                        onDismiss = { showBottomSheet = false },
                        onAddAssignment = { title, course, dueDate ->
                            viewModel.addAssignment(title, course, dueDate)
                            showBottomSheet = false
                        })
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No assignments yet! 🎉",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button below to create your first task.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssignmentBottomSheet(
    onDismiss: () -> Unit, onAddAssignment: (String, String, String) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var courseInput by remember { mutableStateOf("") }
    var dueDateInput by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "New Assignment", style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = courseInput,
                onValueChange = { courseInput = it },
                label = { Text("Course Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dueDateInput,
                onValueChange = { dueDateInput = it },
                label = { Text("Due Date (e.g. Oct 12)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (titleInput.isNotBlank() && courseInput.isNotBlank()) {
                        onAddAssignment(titleInput, courseInput, dueDateInput)
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Assignment")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AssignmentList(
    assignments: List<AssignmentEntity>,
    onToggleComplete: (AssignmentEntity) -> Unit,
    onDeleteClick: (AssignmentEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(assignments, key = { it.id }) { item ->
            AssignmentCard(
                assignment = item,
                onToggleComplete = { onToggleComplete(item) },
                onDeleteClick = { onDeleteClick(item) })
        }

    }

}

@Composable
fun AssignmentCard(
    assignment: AssignmentEntity,
    onToggleComplete: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = assignment.isCompleted, onCheckedChange = { onToggleComplete() })
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (assignment.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = assignment.courseName, style = MaterialTheme.typography.bodyMedium
                    )
                    if (assignment.dueDate.isNotBlank()) {
                        Text(
                            text = "Due: ${assignment.dueDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = "Delete Assignment"
                )
            }
        }
    }
}

@Composable
fun CourseFilterChips(
    courses: List<String>, selectedCourse: String, onCourseSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(courses) { course ->
            FilterChip(
                selected = course == selectedCourse,
                onClick = { onCourseSelected(course) },
                label = { Text(course) })
        }
    }
}
