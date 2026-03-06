package iut.dagere.tache_pistache.controller

import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task

class NotificationController {

    /**
     * Retourne les tâches en retard : celles dont la date d'échéance est passée et qui ne sont pas
     * encore terminées.
     */
    fun getLateTasks(tasks: List<Task>): List<Task> {
        val now = System.currentTimeMillis()
        return tasks.filter { task ->
            task.dueDate != null && task.dueDate < now && task.status != Status.DONE
        }
    }
}
