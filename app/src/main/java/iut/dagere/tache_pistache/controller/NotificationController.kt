package iut.dagere.tache_pistache.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import iut.dagere.tache_pistache.R
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task

class NotificationController(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "late_tasks_channel"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Tâches en retard",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications quand des tâches passent en retard"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

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

    /**
     * Envoie une notification Android pour les tâches nouvellement en retard.
     */
    fun sendLateTaskNotification(newLateTasks: List<Task>) {
        if (newLateTasks.isEmpty()) return

        // Vérifier la permission (Android 13+)
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val title = if (newLateTasks.size == 1) {
            "Tâche en retard !"
        } else {
            "${newLateTasks.size} tâches en retard !"
        }

        val text = if (newLateTasks.size == 1) {
            "\"${newLateTasks.first().title}\" a dépassé sa date d'échéance"
        } else {
            newLateTasks.joinToString(", ") { it.title }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pistachio_rouge)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}
