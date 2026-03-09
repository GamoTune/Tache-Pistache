package iut.dagere.tache_pistache.controller

import iut.dagere.tache_pistache.data.TaskRepository
import iut.dagere.tache_pistache.model.Recurrence
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task
import iut.dagere.tache_pistache.model.TimeUnit
import java.util.Calendar

class TaskController(private val repository: TaskRepository) {

    fun onAddTaskClicked(task: Task) {
        repository.saveTask(task)
    }

    fun onTaskDone(task: Task) {
        val doneTask = task.copy(status = Status.DONE)
        repository.saveTask(doneTask)

        // Gestion de la récurrence
        if (task.recurrence != Recurrence.NONE && task.dueDate != null) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = task.dueDate
            }
            when (task.recurrence) {
                Recurrence.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                Recurrence.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                Recurrence.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                Recurrence.YEARLY -> calendar.add(Calendar.YEAR, 1)
                Recurrence.CUSTOM -> {
                    if (task.customRecurrenceValue != null && task.customRecurrenceUnit != null) {
                        val amount = task.customRecurrenceValue
                        when (task.customRecurrenceUnit) {
                            TimeUnit.HOURS -> calendar.add(Calendar.HOUR_OF_DAY, amount)
                            TimeUnit.DAYS -> calendar.add(Calendar.DAY_OF_YEAR, amount)
                            TimeUnit.WEEKS -> calendar.add(Calendar.WEEK_OF_YEAR, amount)
                            TimeUnit.MONTHS -> calendar.add(Calendar.MONTH, amount)
                            TimeUnit.YEARS -> calendar.add(Calendar.YEAR, amount)
                        }
                    }
                }
                Recurrence.NONE -> {}
            }

            val nextTask = task.copy(
                id = 0, // Auto-généré par le repository
                status = Status.TODO,
                dueDate = calendar.timeInMillis
            )
            repository.saveTask(nextTask)
        }
    }

    fun updateTask(task: Task) {
        repository.saveTask(task)
    }

    fun onFilterSelected(status: Status): List<Task> {
        return repository.getAllTasks().filter { it.status == status }
    }

    fun getAllTasks(): List<Task> {
        return repository.getAllTasks()
    }

    fun deleteTask(task: Task) {
        repository.deleteTask(task)
    }

    fun purgeDoneTasks() {
        repository.purgeDoneTasks()
    }

    /**
     * Vérifie et met à jour le statut des tâches en retard. Une tâche est en retard si sa date
     * d'échéance est passée et qu'elle n'est pas DONE.
     */
    fun checkAndUpdateLateTasks() {
        val now = System.currentTimeMillis()
        repository.getAllTasks().forEach { task ->
            if (task.dueDate != null && task.dueDate < now && task.status == Status.TODO) {
                repository.saveTask(task.copy(status = Status.LATE))
            }
        }
    }
}
