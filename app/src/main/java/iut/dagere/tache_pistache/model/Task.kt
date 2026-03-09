package iut.dagere.tache_pistache.model

enum class TimeUnit(val label: String) {
    HOURS("Heure(s)"),
    DAYS("Jour(s)"),
    WEEKS("Semaine(s)"),
    MONTHS("Mois"),
    YEARS("An(s)")
}

data class Task(
        val id: Int,
        val title: String = "",
        val description: String = "",
        val status: Status = Status.TODO,
        val dueDate: Long? = null,
        val recurrence: Recurrence = Recurrence.NONE,
        val customRecurrenceValue: Int? = null,
        val customRecurrenceUnit: TimeUnit? = null
)
