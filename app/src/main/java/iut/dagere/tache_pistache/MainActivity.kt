package iut.dagere.tache_pistache

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import iut.dagere.tache_pistache.controller.NotificationController
import iut.dagere.tache_pistache.controller.RewardController
import iut.dagere.tache_pistache.controller.TaskController
import iut.dagere.tache_pistache.data.TaskRepository
import iut.dagere.tache_pistache.model.Filter
import iut.dagere.tache_pistache.model.Priority
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.ui.components.ConfettiOverlay
import iut.dagere.tache_pistache.ui.screens.TaskDetailScreen
import iut.dagere.tache_pistache.ui.screens.TaskListScreen
import iut.dagere.tache_pistache.ui.theme.TachePistacheTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Demander la permission de notification (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            TachePistacheTheme {
                val repository = remember { TaskRepository(this@MainActivity) }
                val controller = remember { TaskController(repository) }
                val rewardController = remember { RewardController(this@MainActivity) }
                val notificationController = remember { NotificationController(this@MainActivity) }
                var nextId by remember { mutableIntStateOf((controller.getAllTasks().maxOfOrNull { it.id } ?: 0) + 1) }
                var selectedTask by remember { mutableStateOf<Task?>(null) }
                // Force recomposition when tasks change
                var refreshKey by remember { mutableIntStateOf(0) }
                // Filtre actif
                var selectedFilter by remember { mutableStateOf(Filter.ALL) }
                // État des confettis
                var showConfetti by remember { mutableStateOf(false) }
                // Confirmation de purge
                var showPurgeDialog by remember { mutableStateOf(false) }
                // Mode création directe en édition
                var startInEditMode by remember { mutableStateOf(false) }

                // Vérifier les tâches en retard à chaque rafraîchissement
                @Suppress("UNUSED_VARIABLE")
                val lateCheck = remember(refreshKey) {
                    val newLateTasks = controller.checkAndUpdateLateTasks()
                    notificationController.sendLateTaskNotification(newLateTasks)
                    true
                }

                // Read tasks (refreshKey triggers recomposition)
                val allTasks = remember(refreshKey) { controller.getAllTasks() }

                // Appliquer le filtre
                val filteredTasks =
                        remember(allTasks, selectedFilter) {
                            val priorityOrder = mapOf(Priority.HIGH to 0, Priority.MEDIUM to 1, Priority.LOW to 2)
                            when (selectedFilter) {
                                Filter.ALL -> allTasks
                                        .sortedBy { priorityOrder[it.priority] }
                                Filter.TODO ->
                                        allTasks
                                                .filter {
                                                    it.status == Status.TODO ||
                                                            it.status == Status.LATE
                                                }
                                                .sortedWith(
                                                    compareBy(
                                                        { if (it.status == Status.LATE) 0 else 1 },
                                                        { priorityOrder[it.priority] }
                                                    )
                                                )
                                Filter.LATE -> allTasks.filter { it.status == Status.LATE }
                                        .sortedBy { priorityOrder[it.priority] }
                                Filter.DONE -> allTasks.filter { it.status == Status.DONE }
                            }
                        }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (selectedTask != null) {
                        // Récupérer la version à jour de la tâche
                        val currentTask =
                                allTasks.find { it.id == selectedTask!!.id } ?: selectedTask!!
                        TaskDetailScreen(
                                task = currentTask,
                                onBack = {
                                    selectedTask = null
                                    startInEditMode = false
                                },
                                onSave = { updatedTask ->
                                    controller.updateTask(updatedTask)
                                    refreshKey++
                                },
                                onDone = { task ->
                                    controller.onTaskDone(task, rewardController)
                                    refreshKey++
                                    showConfetti = true
                                },
                                onDelete = { task ->
                                    controller.deleteTask(task)
                                    refreshKey++
                                },
                                startInEditMode = startInEditMode
                        )
                    } else {
                        Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    TopAppBar(
                                            title = { Text("Tache Pistache") },
                                            actions = {
                                                // Compteur de pistaches
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.padding(end = 8.dp)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_pistachio),
                                                        contentDescription = "Pistaches",
                                                        modifier = Modifier.size(20.dp).rotate(-45f),
                                                        tint = androidx.compose.ui.graphics.Color.Unspecified
                                                    )
                                                    Text(
                                                        text = "${rewardController.pistachioActualReward}",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                                IconButton(onClick = {
                                                    showPurgeDialog = true
                                                }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Purger les tâches terminées")
                                                }
                                            },
                                            colors =
                                                    TopAppBarDefaults.topAppBarColors(
                                                            containerColor =
                                                                    MaterialTheme.colorScheme
                                                                            .primary,
                                                            titleContentColor =
                                                                    MaterialTheme.colorScheme
                                                                            .onPrimary,
                                                            actionIconContentColor =
                                                                    MaterialTheme.colorScheme
                                                                            .onPrimary
                                                    )
                                    )
                                },
                                floatingActionButton = {
                                    FloatingActionButton(
                                            onClick = {
                                                val newTask = Task(id = nextId)
                                                controller.onAddTaskClicked(newTask)
                                                selectedTask = newTask
                                                startInEditMode = true
                                                nextId++
                                                refreshKey++
                                            },
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Ajouter une tâche"
                                        )
                                    }
                                }
                        ) { innerPadding ->
                            TaskListScreen(
                                    tasks = filteredTasks,
                                    onTaskClick = { task ->
                                        selectedTask = task
                                        startInEditMode = false
                                    },
                                    onTaskDone = { task, isDone ->
                                        if (isDone) {
                                            controller.onTaskDone(task, rewardController)
                                            showConfetti = true
                                        } else {
                                            controller.onTaskUndone(task, rewardController)
                                        }
                                        refreshKey++
                                    },
                                    onDeleteTask = { task ->
                                        controller.deleteTask(task)
                                        refreshKey++
                                    },
                                    selectedFilter = selectedFilter,
                                    onFilterSelected = { filter -> selectedFilter = filter },
                                    modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    // Overlay de confettis par-dessus tout le contenu
                    ConfettiOverlay(isVisible = showConfetti, onFinished = { showConfetti = false })

                    // Dialog de confirmation de purge
                    if (showPurgeDialog) {
                        AlertDialog(
                            onDismissRequest = { showPurgeDialog = false },
                            title = { Text("Purger les tâches terminées") },
                            text = { Text("Cette action supprimera définitivement toutes les tâches marquées comme terminées. Continuer ?") },
                            confirmButton = {
                                androidx.compose.material3.TextButton(
                                    onClick = {
                                        controller.purgeDoneTasks()
                                        refreshKey++
                                        showPurgeDialog = false
                                    }
                                ) { Text("Supprimer", color = MaterialTheme.colorScheme.error) }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(
                                    onClick = { showPurgeDialog = false }
                                ) { Text("Annuler") }
                            }
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
        val mockTasks =
                listOf(
                        Task(
                                id = 1,
                                title = "Acheter des pistaches",
                                description = "Aller au marché"
                        ),
                        Task(
                                id = 2,
                                title = "Préparer le gâteau",
                                description = "Recette pistache-chocolat"
                        ),
                        Task(
                                id = 3,
                                title = "Décorer la table",
                                description = "Thème vert et beige"
                        )
                )
        Scaffold(
                topBar = {
                    TopAppBar(
                            title = { Text("Tache Pistache") },
                            actions = {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Delete, contentDescription = "Purger les tâches terminées")
                                }
                            },
                            colors =
                                    TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                    ) { Icon(Icons.Default.Add, contentDescription = "Ajouter une tâche") }
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
