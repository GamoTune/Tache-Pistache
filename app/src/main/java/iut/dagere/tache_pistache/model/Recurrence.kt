package iut.dagere.tache_pistache.model

enum class Recurrence(val label: String) {
    NONE("Aucune"),
    DAILY("Quotidienne"),
    WEEKLY("Hebdomadaire"),
    MONTHLY("Mensuelle"),
    YEARLY("Annuelle"),
    CUSTOM("Personnalisée")
}
