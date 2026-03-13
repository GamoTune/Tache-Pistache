package iut.dagere.tache_pistache.controller

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import iut.dagere.tache_pistache.model.Priority
import iut.dagere.tache_pistache.model.Status
import iut.dagere.tache_pistache.model.Task

class RewardController(context: Context) {
    private val prefs = context.getSharedPreferences("rewards", Context.MODE_PRIVATE)

    /** Total de pistaches accumulées */
    var pistachioActualReward by mutableIntStateOf(prefs.getInt("actualReward", 0))
        private set

    /** Total maximum de pistaches qu'on aurait pu gagner (sans retard) */
    var pistachioMaxReward by mutableIntStateOf(prefs.getInt("maxReward", 0))
        private set

    private fun persist() {
        prefs.edit()
            .putInt("actualReward", pistachioActualReward)
            .putInt("maxReward", pistachioMaxReward)
            .apply()
    }

    /**
     * Calcule la récompense en pistaches pour une tâche donnée.
     * - HIGH = 3, MEDIUM = 2, LOW = 1
     * - Si la tâche est en retard, la récompense est divisée par 2 (min 1)
     */
    fun calculateReward(task: Task): Int {
        val baseReward = when (task.priority) {
            Priority.HIGH -> 3
            Priority.MEDIUM -> 2
            Priority.LOW -> 1
        }
        return if (task.status == Status.LATE) {
            maxOf(1, baseReward / 2)
        } else {
            baseReward
        }
    }

    /**
     * Appelé quand une tâche est réalisée : calcule et ajoute la récompense.
     * Retourne le nombre de pistaches gagnées.
     */
    fun onTaskDone(task: Task): Int {
        val maxForTask = when (task.priority) {
            Priority.HIGH -> 3
            Priority.MEDIUM -> 2
            Priority.LOW -> 1
        }
        pistachioMaxReward += maxForTask

        val reward = calculateReward(task)
        pistachioActualReward += reward
        persist()
        return reward
    }

    /**
     * Appelé quand on annule la complétion d'une tâche.
     * Retire les pistaches précédemment gagnées.
     */
    fun onTaskUndone(task: Task, previousReward: Int) {
        val maxForTask = when (task.priority) {
            Priority.HIGH -> 3
            Priority.MEDIUM -> 2
            Priority.LOW -> 1
        }
        pistachioMaxReward = maxOf(0, pistachioMaxReward - maxForTask)
        pistachioActualReward = maxOf(0, pistachioActualReward - previousReward)
        persist()
    }
}
