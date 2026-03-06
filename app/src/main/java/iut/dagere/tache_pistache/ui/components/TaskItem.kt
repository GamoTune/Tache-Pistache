package iut.dagere.tache_pistache.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.ui.theme.TachePistacheTheme

@Composable
fun TaskItem(
        task: Task,
        onClick: () -> Unit,
        onDoneChanged: (Boolean) -> Unit = {},
        modifier: Modifier = Modifier
) {
        val isDone = task.status == Status.DONE
        val isLate = task.status == Status.LATE

        Card(
                modifier =
                        modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { onClick() },
                colors =
                        CardDefaults.cardColors(
                                containerColor =
                                        when {
                                                isLate -> MaterialTheme.colorScheme.errorContainer
                                                isDone -> MaterialTheme.colorScheme.surfaceVariant
                                                else -> MaterialTheme.colorScheme.surface
                                        }
                        )
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Checkbox(
                                checked = isDone,
                                onCheckedChange = { checked -> onDoneChanged(checked) },
                                colors =
                                        if (isLate)
                                                CheckboxDefaults.colors(
                                                        uncheckedColor =
                                                                MaterialTheme.colorScheme.error
                                                )
                                        else CheckboxDefaults.colors()
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                                Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color =
                                                when {
                                                        isLate -> MaterialTheme.colorScheme.error
                                                        isDone ->
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                },
                                        textDecoration =
                                                if (isDone) TextDecoration.LineThrough
                                                else TextDecoration.None,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                if (task.description.isNotBlank()) {
                                        Text(
                                                text = task.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color =
                                                        if (isLate)
                                                                MaterialTheme.colorScheme
                                                                        .onErrorContainer
                                                        else
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )
                                }
                                if (isLate) {
                                        Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        painter =
                                                                androidx.compose.ui.res
                                                                        .painterResource(
                                                                                id =
                                                                                        iut.dagere
                                                                                                .tache_pistache
                                                                                                .R
                                                                                                .drawable
                                                                                                .ic_pistachio_rouge
                                                                        ),
                                                        contentDescription = null,
                                                        modifier =
                                                                Modifier.size(14.dp).rotate(-45f),
                                                        tint =
                                                                androidx.compose.ui.graphics.Color
                                                                        .Unspecified
                                                )
                                                Text(
                                                        text = "En retard",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.error
                                                )
                                        }
                                }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
        TachePistacheTheme {
                Column {
                        TaskItem(
                                task =
                                        Task(
                                                id = 1,
                                                title = "Acheter des pistaches",
                                                description = "Aller au marché"
                                        ),
                                onClick = {},
                                onDoneChanged = {}
                        )
                        TaskItem(
                                task =
                                        Task(
                                                id = 2,
                                                title = "Tâche en retard",
                                                description = "Oups !",
                                                status = Status.LATE
                                        ),
                                onClick = {},
                                onDoneChanged = {}
                        )
                        TaskItem(
                                task =
                                        Task(
                                                id = 3,
                                                title = "Tâche terminée",
                                                description = "Bien joué",
                                                status = Status.DONE
                                        ),
                                onClick = {},
                                onDoneChanged = {}
                        )
                }
        }
}
