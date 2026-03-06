package iut.dagere.tache_pistache.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.ui.theme.TachePistacheTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
        task: Task,
        onBack: () -> Unit,
        onSave: (Task) -> Unit = {},
        onDone: (Task) -> Unit = {},
        modifier: Modifier = Modifier
) {
        var isEditing by remember { mutableStateOf(false) }
        var editedTitle by remember(task) { mutableStateOf(task.title) }
        var editedDescription by remember(task) { mutableStateOf(task.description) }
        var editedDueDate by remember(task) { mutableStateOf(task.dueDate) }
        var showDatePicker by remember { mutableStateOf(false) }

        val isDone = task.status == Status.DONE
        val isLate = task.status == Status.LATE

        val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

        // DatePicker Dialog
        if (showDatePicker) {
                val datePickerState =
                        rememberDatePickerState(
                                initialSelectedDateMillis = editedDueDate
                                                ?: System.currentTimeMillis()
                        )
                DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                editedDueDate = datePickerState.selectedDateMillis
                                                showDatePicker = false
                                        }
                                ) { Text("OK") }
                        },
                        dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
                        }
                ) { DatePicker(state = datePickerState) }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(if (isEditing) "Modifier la tâche" else task.title)
                                },
                                navigationIcon = {
                                        IconButton(
                                                onClick = {
                                                        if (isEditing) {
                                                                isEditing = false
                                                        } else {
                                                                onBack()
                                                        }
                                                }
                                        ) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Retour"
                                                )
                                        }
                                },
                                actions = {
                                        if (!isEditing && !isDone) {
                                                IconButton(onClick = { isEditing = true }) {
                                                        Icon(
                                                                Icons.Default.Edit,
                                                                contentDescription = "Modifier",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimary
                                                        )
                                                }
                                        }
                                        if (isEditing) {
                                                IconButton(
                                                        onClick = {
                                                                val updatedTask =
                                                                        task.copy(
                                                                                title = editedTitle,
                                                                                description =
                                                                                        editedDescription,
                                                                                dueDate =
                                                                                        editedDueDate
                                                                        )
                                                                onSave(updatedTask)
                                                                isEditing = false
                                                        }
                                                ) {
                                                        Icon(
                                                                Icons.Default.Check,
                                                                contentDescription = "Sauvegarder",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimary
                                                        )
                                                }
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary,
                                                navigationIconContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        )
                        )
                }
        ) { innerPadding ->
                Column(
                        modifier =
                                modifier.fillMaxSize()
                                        .padding(innerPadding)
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                ) {
                        if (isEditing) {
                                // Mode édition
                                OutlinedTextField(
                                        value = editedTitle,
                                        onValueChange = { editedTitle = it },
                                        label = { Text("Titre") },
                                        modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                        value = editedDescription,
                                        onValueChange = { editedDescription = it },
                                        label = { Text("Description") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Sélection de la date d'échéance
                                OutlinedButton(
                                        onClick = { showDatePicker = true },
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                                if (editedDueDate != null)
                                                        "Échéance : ${dateFormat.format(Date(editedDueDate!!))}"
                                                else "Définir une date d'échéance"
                                        )
                                }

                                if (editedDueDate != null) {
                                        TextButton(onClick = { editedDueDate = null }) {
                                                Text("Supprimer la date d'échéance")
                                        }
                                }
                        } else {
                                // Mode affichage
                                Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        Card(
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        when (task.status) {
                                                                                Status.DONE ->
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primaryContainer
                                                                                Status.LATE ->
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .errorContainer
                                                                                Status.TODO ->
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .secondaryContainer
                                                                        }
                                                        )
                                        ) {
                                                Row(
                                                        modifier =
                                                                Modifier.padding(
                                                                        horizontal = 12.dp,
                                                                        vertical = 4.dp
                                                                ),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(4.dp),
                                                        verticalAlignment =
                                                                androidx.compose.ui.Alignment
                                                                        .CenterVertically
                                                ) {
                                                        if (task.status == Status.DONE ||
                                                                        task.status == Status.LATE
                                                        ) {
                                                                val iconResId =
                                                                        if (task.status ==
                                                                                        Status.DONE
                                                                        ) {
                                                                                iut.dagere
                                                                                        .tache_pistache
                                                                                        .R
                                                                                        .drawable
                                                                                        .ic_pistachio
                                                                        } else {
                                                                                iut.dagere
                                                                                        .tache_pistache
                                                                                        .R
                                                                                        .drawable
                                                                                        .ic_pistachio_rouge
                                                                        }
                                                                Icon(
                                                                        painter =
                                                                                androidx.compose.ui
                                                                                        .res
                                                                                        .painterResource(
                                                                                                id =
                                                                                                        iconResId
                                                                                        ),
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(16.dp)
                                                                                        .rotate(
                                                                                                -45f
                                                                                        ),
                                                                        tint =
                                                                                androidx.compose.ui
                                                                                        .graphics
                                                                                        .Color
                                                                                        .Unspecified
                                                                )
                                                        }
                                                        Text(
                                                                text =
                                                                        when (task.status) {
                                                                                Status.TODO ->
                                                                                        "À faire"
                                                                                Status.LATE ->
                                                                                        "En retard"
                                                                                Status.DONE ->
                                                                                        "Terminée"
                                                                        },
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelMedium,
                                                                color =
                                                                        when (task.status) {
                                                                                Status.LATE ->
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .error
                                                                                else ->
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface
                                                                        }
                                                        )
                                                }
                                        }
                                }

                                // Afficher la date d'échéance
                                if (task.dueDate != null) {
                                        val isOverdue =
                                                task.dueDate < System.currentTimeMillis() && !isDone
                                        Card(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(bottom = 12.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        if (isOverdue)
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .errorContainer
                                                                        else
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .tertiaryContainer
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(8.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Default.DateRange,
                                                                contentDescription = null,
                                                                tint =
                                                                        if (isOverdue)
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error
                                                                        else
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onTertiaryContainer
                                                        )
                                                        Text(
                                                                text =
                                                                        "Échéance : ${dateFormat.format(Date(task.dueDate))}",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        if (isOverdue)
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error
                                                                        else
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onTertiaryContainer
                                                        )
                                                }
                                        }
                                }

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.surface
                                                )
                                ) {
                                        Text(
                                                text = task.description,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(16.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                if (!isDone) {
                                        Button(
                                                onClick = { onDone(task) },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                        ) {
                                                Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Text("Marquer comme réalisée")
                                        }
                                } else {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(8.dp),
                                                        verticalAlignment =
                                                                androidx.compose.ui.Alignment
                                                                        .CenterVertically
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
                                                                                                        .ic_pistachio
                                                                                ),
                                                                contentDescription = null,
                                                                modifier =
                                                                        Modifier.size(24.dp)
                                                                                .rotate(-45f),
                                                                tint =
                                                                        androidx.compose.ui.graphics
                                                                                .Color.Unspecified
                                                        )
                                                        Text(
                                                                text = "Tâche réalisée !",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimaryContainer
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
        TachePistacheTheme {
                TaskDetailScreen(
                        task =
                                Task(
                                        id = 1,
                                        title = "Acheter des pistaches",
                                        description =
                                                "Aller au marché bio pour acheter 500g de pistaches non salées pour le gâteau de dimanche.",
                                        dueDate = System.currentTimeMillis() - 86400000 // hier
                                ),
                        onBack = {}
                )
        }
}
