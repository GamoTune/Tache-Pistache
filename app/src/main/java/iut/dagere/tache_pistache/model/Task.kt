package iut.dagere.tache_pistache.model

data class Task(
    val id: Int,
    val title: String = "",
    val description: String = "",
    val status: Status = Status.TODO
)
