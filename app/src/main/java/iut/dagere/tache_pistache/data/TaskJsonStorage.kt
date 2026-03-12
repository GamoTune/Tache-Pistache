package iut.dagere.tache_pistache.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import iut.dagere.tache_pistache.model.Task
import java.io.File

/**
 * Gère la persistance des tâches dans un fichier JSON interne.
 */
class TaskJsonStorage(private val context: Context) {

    private val gson = Gson()
    private val fileName = "tasks.json"

    private fun getFile(): File = File(context.filesDir, fileName)

    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        getFile().writeText(json)
    }

    fun loadTasks(): List<Task> {
        val file = getFile()
        if (!file.exists()) return emptyList()
        return try {
            val json = file.readText()
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
