package iut.dagere.tache_pistache

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import iut.dagere.tache_pistache.controller.TaskController
import iut.dagere.tache_pistache.data.TaskRepository
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.ui.screens.TaskDetailScreen
import iut.dagere.tache_pistache.ui.screens.TaskListScreen
import iut.dagere.tache_pistache.ui.theme.TachePistacheTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TachePistacheTheme {
                val repository = remember { TaskRepository() }
                val controller = remember {
                    TaskController(repository)
                }
                var nextId by remember { mutableIntStateOf(1) }
                var selectedTask by remember { mutableStateOf<Task?>(null) }
                // Force recomposition when tasks change
                var refreshKey by remember { mutableIntStateOf(0) }

                // Read tasks (refreshKey triggers recomposition)
                val tasks = remember(refreshKey) { controller.getAllTasks() }

                if (selectedTask != null) {
                    // Récupérer la version à jour de la tâche
                    val currentTask = tasks.find { it.id == selectedTask!!.id } ?: selectedTask!!
                    TaskDetailScreen(
                        task = currentTask,
                        onBack = { selectedTask = null },
                        onSave = { updatedTask ->
                            controller.updateTask(updatedTask)
                            refreshKey++
                        },
                        onDone = { task ->
                            controller.onTaskDone(task)
                            refreshKey++
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("Tache Pistache") },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    val newTask = Task(
                                        id = nextId,
                                        title = "Tâche #$nextId",
                                        description = "Description de la tâche #$nextId"
                                    )
                                    controller.onAddTaskClicked(newTask)
                                    nextId++
                                    refreshKey++
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ajouter une tâche")
                            }
                        }
                    ) { innerPadding ->
                        TaskListScreen(
                            tasks = tasks,
                            onTaskClick = { task -> selectedTask = task },
                            onTaskDone = { task, isDone ->
                                if (isDone) {
                                    controller.onTaskDone(task)
                                } else {
                                    controller.updateTask(task.copy(status = Status.TODO))
                                }
                                refreshKey++
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    TachePistacheTheme {
        val mockTasks = listOf(
            Task(id = 1, title = "Acheter des pistaches", description = "Aller au marché"),
            Task(id = 2, title = "Préparer le gâteau", description = "Recette pistache-chocolat"),
            Task(id = 3, title = "Décorer la table", description = "Thème vert et beige")
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tache Pistache") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter une tâche")
                }
            }
        ) { innerPadding ->
            TaskListScreen(
                tasks = mockTasks,
                onTaskClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}