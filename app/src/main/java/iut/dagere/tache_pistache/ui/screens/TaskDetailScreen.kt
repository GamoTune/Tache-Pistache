package iut.dagere.tache_pistache.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import iut.dagere.tache_pistache.model.Picture
import iut.dagere.tache_pistache.model.Priority
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.model.Recurrence
import iut.dagere.tache_pistache.model.TimeUnit
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
        onDelete: (Task) -> Unit = {},
        startInEditMode: Boolean = false,
        modifier: Modifier = Modifier
) {
        var isEditing by remember { mutableStateOf(startInEditMode) }
        var editedTitle by remember(task) { mutableStateOf(task.title) }
        var editedDescription by remember(task) { mutableStateOf(task.description) }
        var editedDueDate by remember(task) { mutableStateOf(task.dueDate) }
        var editedRecurrence by remember(task) { mutableStateOf(task.recurrence) }
        var editedCustomRecurrenceValue by remember(task) { mutableStateOf(task.customRecurrenceValue?.toString() ?: "") }
        var editedCustomRecurrenceUnit by remember(task) { mutableStateOf(task.customRecurrenceUnit ?: TimeUnit.DAYS) }
        var editedPriority by remember(task) { mutableStateOf(task.priority) }
        var editedPictures by remember(task) { mutableStateOf(task.pictures) }

        var showDatePicker by remember { mutableStateOf(false) }
        var recurrenceExpanded by remember { mutableStateOf(false) }
        var customUnitExpanded by remember { mutableStateOf(false) }

        val context = LocalContext.current

        // Lanceur pour choisir une photo depuis la galerie
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                // Prendre la permission persistante pour l'URI
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Certains providers ne supportent pas la persistance, on continue quand même
                }
                val nextPicId = (editedPictures.maxOfOrNull { it.id } ?: 0) + 1
                editedPictures = editedPictures + Picture(id = nextPicId, path = uri.toString())
            }
        }

        val isDone = task.status == Status.DONE
        val isLate = task.status == Status.LATE
        val baseReward = when (task.priority) {
            Priority.HIGH -> 3
            Priority.MEDIUM -> 2
            Priority.LOW -> 1
        }
        val isCurrentlyLate = isLate || (task.dueDate != null && task.dueDate < System.currentTimeMillis())
        val potentialReward = if (isCurrentlyLate) maxOf(1, baseReward / 2) else baseReward

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
                                                                // Si on est en mode création et qu'il n'y a pas de titre, supprimer la tâche
                                                                if (startInEditMode && editedTitle.isBlank()) {
                                                                        onDelete(task)
                                                                }
                                                                isEditing = false
                                                                if (startInEditMode) onBack()
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
                                                                                priority = editedPriority,
                                                                                dueDate =
                                                                                        editedDueDate,
                                                                                recurrence = editedRecurrence,
                                                                                customRecurrenceValue = editedCustomRecurrenceValue.toIntOrNull(),
                                                                                customRecurrenceUnit = editedCustomRecurrenceUnit,
                                                                                pictures = editedPictures
                                                                        )
                                                                onSave(updatedTask)
                                                                isEditing = false
                                                        },
                                                        enabled = editedTitle.trim().length >= 2
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
                                val isTitleValid = editedTitle.trim().length >= 2
                                OutlinedTextField(
                                        value = editedTitle,
                                        onValueChange = { editedTitle = it },
                                        label = { Text("Titre") },
                                        placeholder = { Text("Nom de votre tâche pistachée...") },
                                        isError = !isTitleValid,
                                        supportingText = {
                                                if (!isTitleValid) {
                                                        Text("Le titre doit contenir au moins 2 caractères.")
                                                }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                        value = editedDescription,
                                        onValueChange = { editedDescription = it },
                                        label = { Text("Description") },
                                        placeholder = { Text("Décrivez votre tâche en détail...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Sélecteur de priorité
                                Text(
                                    text = "Priorité",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Priority.entries.forEach { priority ->
                                        val priorityColor = when (priority) {
                                            Priority.HIGH -> MaterialTheme.colorScheme.error
                                            Priority.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFFA726)
                                            Priority.LOW -> MaterialTheme.colorScheme.primary
                                        }
                                        FilterChip(
                                            selected = editedPriority == priority,
                                            onClick = { editedPriority = priority },
                                            label = { Text(priority.label) },
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = priorityColor.copy(alpha = 0.2f),
                                                selectedLabelColor = priorityColor
                                            )
                                        )
                                    }
                                }
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

                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        ExposedDropdownMenuBox(
                                            expanded = recurrenceExpanded,
                                            onExpandedChange = { recurrenceExpanded = !recurrenceExpanded },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = editedRecurrence.label,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Récurrence") },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                                modifier = Modifier.menuAnchor().fillMaxWidth()
                                            )
                                            ExposedDropdownMenu(
                                                expanded = recurrenceExpanded,
                                                onDismissRequest = { recurrenceExpanded = false }
                                            ) {
                                                Recurrence.entries.forEach { rec ->
                                                    DropdownMenuItem(
                                                        text = { Text(rec.label) },
                                                        onClick = {
                                                            editedRecurrence = rec
                                                            recurrenceExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        if (editedRecurrence == Recurrence.CUSTOM) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                OutlinedTextField(
                                                    value = editedCustomRecurrenceValue,
                                                    onValueChange = { newValue -> 
                                                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                            editedCustomRecurrenceValue = newValue
                                                        }
                                                    },
                                                    label = { Text("Valeur") },
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true
                                                )

                                                ExposedDropdownMenuBox(
                                                    expanded = customUnitExpanded,
                                                    onExpandedChange = { customUnitExpanded = !customUnitExpanded },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    OutlinedTextField(
                                                        value = editedCustomRecurrenceUnit.label,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Unité") },
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customUnitExpanded) },
                                                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                                    )
                                                    ExposedDropdownMenu(
                                                        expanded = customUnitExpanded,
                                                        onDismissRequest = { customUnitExpanded = false }
                                                    ) {
                                                        TimeUnit.entries.forEach { unit ->
                                                            DropdownMenuItem(
                                                                text = { Text(unit.label) },
                                                                onClick = {
                                                                    editedCustomRecurrenceUnit = unit
                                                                    customUnitExpanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                }

                                // Section photos en mode édition
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Photos",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(editedPictures) { picture ->
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.size(100.dp)
                                        ) {
                                            androidx.compose.foundation.layout.Box {
                                                AsyncImage(
                                                    model = Uri.parse(picture.path),
                                                    contentDescription = "Photo jointe",
                                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                                // Bouton supprimer la photo
                                                IconButton(
                                                    onClick = {
                                                        editedPictures = editedPictures.filter { it.id != picture.id }
                                                    },
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Supprimer la photo",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        OutlinedButton(
                                            onClick = { photoPickerLauncher.launch("image/*") },
                                            modifier = Modifier.size(100.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null)
                                                Text("Photo", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
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
                                        // Badge statut
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

                                        // Badge priorité (uniquement si non-terminée)
                                        if (!isDone) {
                                            val priorityColor = when (task.priority) {
                                                Priority.HIGH -> MaterialTheme.colorScheme.error
                                                Priority.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFFA726)
                                                Priority.LOW -> MaterialTheme.colorScheme.primary
                                            }
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = priorityColor.copy(alpha = 0.15f)
                                                )
                                            ) {
                                                Text(
                                                    text = task.priority.label,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = priorityColor,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                }

                                if (task.dueDate != null) {
                                        val isOverdue = task.dueDate < System.currentTimeMillis() && !isDone
                                        Card(
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                                colors = CardDefaults.cardColors(
                                                        containerColor = if (isOverdue) {
                                                                MaterialTheme.colorScheme.errorContainer
                                                        } else {
                                                                MaterialTheme.colorScheme.tertiaryContainer
                                                        }
                                                )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Default.DateRange,
                                                                contentDescription = null,
                                                                tint = if (isOverdue) MaterialTheme.colorScheme.error
                                                                else MaterialTheme.colorScheme.onTertiaryContainer
                                                        )
                                                        Text(
                                                                text = "Echeance : ${dateFormat.format(Date(task.dueDate))}",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                color = if (isOverdue) MaterialTheme.colorScheme.error
                                                                else MaterialTheme.colorScheme.onTertiaryContainer
                                                        )
                                                }
                                        }

                                        if (task.recurrence != Recurrence.NONE) {
                                                val recurrenceText = if (
                                                        task.recurrence == Recurrence.CUSTOM &&
                                                                task.customRecurrenceValue != null &&
                                                                task.customRecurrenceUnit != null
                                                ) {
                                                        "Tous les ${task.customRecurrenceValue} ${task.customRecurrenceUnit.label}"
                                                } else {
                                                        task.recurrence.label.lowercase()
                                                }
                                                Text(
                                                        text = "↻ Recurrence : $recurrenceText",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.padding(bottom = 12.dp)
                                                )
                                        }
                                }

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                        )
                                ) {
                                        Text(
                                                text = task.description,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(16.dp)
                                        )
                                }

                                if (task.pictures.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                                text = "Photos",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                items(task.pictures) { picture ->
                                                        Card(
                                                                shape = RoundedCornerShape(8.dp),
                                                                modifier = Modifier.size(120.dp)
                                                        ) {
                                                                AsyncImage(
                                                                        model = Uri.parse(picture.path),
                                                                        contentDescription = "Photo jointe",
                                                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                                                        contentScale = ContentScale.Crop
                                                                )
                                                        }
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (!isDone) {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                                )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                painter = androidx.compose.ui.res.painterResource(
                                                                        id = iut.dagere.tache_pistache.R.drawable.ic_pistachio
                                                                ),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp).rotate(-45f),
                                                                tint = androidx.compose.ui.graphics.Color.Unspecified
                                                        )
                                                        Text(
                                                                text = "Si tu la reussis maintenant: +$potentialReward pistache(s)",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                                onClick = { onDone(task) },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.primary
                                                )
                                        ) {
                                                Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Text("Marquer comme realisee")
                                        }
                                } else {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                                )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Icon(
                                                                painter = androidx.compose.ui.res.painterResource(
                                                                        id = iut.dagere.tache_pistache.R.drawable.ic_pistachio
                                                                ),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(24.dp).rotate(-45f),
                                                                tint = androidx.compose.ui.graphics.Color.Unspecified
                                                        )
                                                        Text(
                                                                text = "Tache realisee !",
                                                                style = MaterialTheme.typography.titleMedium,
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                        if (task.reward > 0) {
                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                Text(
                                                                        text = "+${task.reward}",
                                                                        style = MaterialTheme.typography.titleMedium,
                                                                        color = MaterialTheme.colorScheme.primary
                                                                )
                                                                Icon(
                                                                        painter = androidx.compose.ui.res.painterResource(
                                                                                id = iut.dagere.tache_pistache.R.drawable.ic_pistachio
                                                                        ),
                                                                        contentDescription = null,
                                                                        modifier = Modifier.size(20.dp).rotate(-45f),
                                                                        tint = androidx.compose.ui.graphics.Color.Unspecified
                                                                )
                                                        }
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
                        task = Task(
                                id = 1,
                                title = "Acheter des pistaches",
                                description = "Aller au marche bio pour acheter 500g de pistaches.",
                                dueDate = System.currentTimeMillis() - 86400000
                        ),
                        onBack = {}
                )
        }
}
