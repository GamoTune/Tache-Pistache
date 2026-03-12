package iut.dagere.tache_pistache.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task

class TaskRepository(context: Context) {
    private val storage = TaskJsonStorage(context)
    private val tasks = mutableStateListOf<Task>()

    init {
        tasks.addAll(storage.loadTasks())
    }

    private fun persist() {
        storage.saveTasks(tasks.toList())
    }

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
        persist()
    }

    fun deleteTask(t: Task) {
        tasks.removeAll { it.id == t.id }
        persist()
    }

    fun purgeDoneTasks() {
        tasks.removeAll { it.status == Status.DONE }
        persist()
    }
}
