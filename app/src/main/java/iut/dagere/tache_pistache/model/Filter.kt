package iut.dagere.tache_pistache.model

enum class Filter(val label: String) {
    ALL("Toutes"),
    TODO("À faire"),
    LATE("En retard"),
    DONE("Terminées")
}
