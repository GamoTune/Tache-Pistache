package iut.dagere.tache_pistache.controller

import iut.dagere.tache_pistache.data.TaskRepository
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task

class TaskController(
    private val repository: TaskRepository
) {

    fun onAddTaskClicked(task: Task) {
        repository.saveTask(task)
    }

    fun onTaskDone(task: Task) {
        val doneTask = task.copy(status = Status.DONE)
        repository.saveTask(doneTask)
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
}
