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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                val tasks = remember { mutableStateListOf<Task>() }
                var nextId by remember { mutableIntStateOf(1) }
                var selectedTask by remember { mutableStateOf<Task?>(null) }

                if (selectedTask != null) {
                    TaskDetailScreen(
                        task = selectedTask!!,
                        onBack = { selectedTask = null }
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
                                    tasks.add(
                                        Task(
                                            id = nextId,
                                            title = "Tâche #$nextId",
                                            description = "Description de la tâche #$nextId"
                                        )
                                    )
                                    nextId++
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