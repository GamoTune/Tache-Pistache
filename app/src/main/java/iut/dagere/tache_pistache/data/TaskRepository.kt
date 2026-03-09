package iut.dagere.tache_pistache.data

import androidx.compose.runtime.mutableStateListOf
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task

class TaskRepository {
    private val tasks = mutableStateListOf<Task>()

    fun getAllTasks(): List<Task> = tasks.toList()

    fun saveTask(t: Task) {
        val taskToSave = if (t.id == 0) {
            val nextId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
            t.copy(id = nextId)
        } else {
            t
        }

        val index = tasks.indexOfFirst { it.id == taskToSave.id }
        if (index >= 0) {
            tasks[index] = taskToSave
        } else {
            tasks.add(taskToSave)
        }
    }

    fun deleteTask(t: Task) {
        tasks.removeAll { it.id == t.id }
    }

    fun purgeDoneTasks() {
        tasks.removeAll { it.status == Status.DONE }
    }
}
