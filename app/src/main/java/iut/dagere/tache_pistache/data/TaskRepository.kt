package iut.dagere.tache_pistache.data

import androidx.compose.runtime.mutableStateListOf
import iut.dagere.tache_pistache.model.Task

class TaskRepository {
    private val tasks = mutableStateListOf<Task>()

    fun getAllTasks(): List<Task> = tasks.toList()

    fun saveTask(t: Task) {
        val index = tasks.indexOfFirst { it.id == t.id }
        if (index >= 0) {
            tasks[index] = t
        } else {
            tasks.add(t)
        }
    }

    fun deleteTask(t: Task) {
        tasks.removeAll { it.id == t.id }
    }
}
