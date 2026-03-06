package iut.dagere.tache_pistache.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import iut.dagere.tache_pistache.model.Filter
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.ui.components.TaskItem
import iut.dagere.tache_pistache.ui.theme.TachePistacheTheme

@Composable
fun TaskListScreen(
        tasks: List<Task>,
        onTaskClick: (Task) -> Unit,
        onTaskDone: (Task, Boolean) -> Unit = { _, _ -> },
        selectedFilter: Filter = Filter.ALL,
        onFilterSelected: (Filter) -> Unit = {},
        modifier: Modifier = Modifier
) {
    // Compter les tâches en retard
    val lateTaskCount = tasks.count { it.status == Status.LATE }

    Column(modifier = modifier.fillMaxSize()) {

        // Chips de filtre
        LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Filter.entries.toList()) { filter ->
                FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterSelected(filter) },
                        label = { Text(filter.label) },
                        colors =
                                FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            }
        }

        // Bannière d'alerte pour les tâches en retard
        if (lateTaskCount > 0) {
            Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                            )
            ) {
                androidx.compose.foundation.layout.Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                            Icons.Default.Warning,
                            contentDescription = "Alerte",
                            tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                            text = "$lateTaskCount tâche(s) en retard !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Liste des tâches
        LazyColumn {
            items(tasks, key = { it.id }) { task ->
                TaskItem(
                        task = task,
                        onClick = { onTaskClick(task) },
                        onDoneChanged = { isDone -> onTaskDone(task, isDone) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    val mockTasks =
            listOf(
                    Task(id = 1, title = "Acheter des pistaches", description = "Aller au marché"),
                    Task(
                            id = 2,
                            title = "Préparer le gâteau",
                            description = "Recette pistache-chocolat",
                            status = Status.LATE
                    ),
                    Task(
                            id = 3,
                            title = "Décorer la table",
                            description = "Thème vert et beige",
                            status = Status.DONE
                    )
            )
    TachePistacheTheme {
        TaskListScreen(tasks = mockTasks, onTaskClick = {}, selectedFilter = Filter.ALL)
    }
}
