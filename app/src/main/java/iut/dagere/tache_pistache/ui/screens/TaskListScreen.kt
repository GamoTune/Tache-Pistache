package iut.dagere.tache_pistache.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.ui.components.TaskItem
import iut.dagere.tache_pistache.ui.theme.TachePistacheTheme

@Composable
fun TaskListScreen(tasks: List<Task>, onTaskClick: (Task) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(tasks) { task ->
            TaskItem(task = task, onClick = { onTaskClick(task) })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    val mockTasks = listOf(
        Task(id = 1, title = "Acheter des pistaches", description = "Aller au marché"),
        Task(id = 2, title = "Préparer le gâteau", description = "Recette pistache-chocolat"),
        Task(id = 3, title = "Décorer la table", description = "Thème vert et beige")
    )
    TachePistacheTheme {
        TaskListScreen(tasks = mockTasks, onTaskClick = {})
    }
}
