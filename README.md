# Tache-Pistache

Application Android de gestion de tâches (To-Do list) sur le thème décalé de la pistache.
Conçue pour être simple, ludique, et efficace.

---

## Spécifications Fonctionnelles


### 1. Gestion des Tâches
- **Création & Édition :** Ajout de tâches avec un titre obligatoire (min. 2 caractères), description longue.
- **Ajout de Médias :** Possibilité d'attacher des photos aux tâches.
- **Dates d'Échéance :** Sélection d'une date limite. Les tâches dont la date est dépassée passent automatiquement en statut "En retard".
- **Priorité :** 3 niveaux de priorité (Basse, Moyenne, Haute). Indicateur de couleur dynamique sur la liste (Vert, Orange, Rouge).
- **Récurrence Avancée :** Les tâches peuvent se répéter (Quotidienne, Hebdo, Mensuelle, Annuelle, ou Personnalisée par heures/jours/semaines/etc.). Lorsqu'une tâche récurrente est validée, la prochaine itération est générée automatiquement.

### 2. Gamification & Récompenses
- **Système de "Pistaches" :** Valider une tâche rapporte des Pistaches en fonction de sa priorité (Haute = 3, Moyenne = 2, Basse = 1).
- **Pénalité de Retard :** Si une tâche est terminée alors qu'elle était en retard, le gain de pistaches est divisé par deux.
- **Feedback Visuel :** Pluie de confettis sur l'écran lors de la complétion d'une tâche.
- **Protection Anti-Triche :** Les récompenses ne sont données qu'à la première validation d'une tâche (décocher et recocher ne permet pas de farmer les points). Une annulation de complétion retire la tâche enfant générée par récurrence.

### 3. Interface et Organisation
- **Filtres Intelligents :** Filtrage des tâches selon l'état (`Toutes`, `À faire`, `En retard`, `Terminées`).
- **Tri multi-critères :** La liste met en avant les tâches en retard, puis trie le reste par ordre de priorité descendante.
- **Purge de l'historique :** Bouton de suppression de masse des tâches "Terminées", protégé par une modale de confirmation.
- **Notifications :** L'application prévient l'utilisateur via une Notification Android classique (icône pistache) au lancement si des tâches viennent de passer "En retard".

---

## Comparatif Technique & Choix d'Architecture

### 1. Persistance des Données : JSON / SharedPreferences ou Base de Données
- **Notre choix (JSON + SharedPreferences) :** Les tâches sont sérialisées au format JSON (via `Gson`) dans un fichier interne `tasks.json`. Les compteurs de score globaux sont dans les `SharedPreferences`.
- **Pourquoi pas Room (SQLite) ?** L'application gère une structure de données simple (pas de relations complexes entre des dizaines d'entités volumineuses). Un fichier JSON est extrêmement léger, direct, ne requiert pas de migration complexe de schémas de base de données, et suffit amplement pour une To-Do list performante stockée localement sur l'appareil.

### 2. Notifications Locales vs. Serveur Background (WorkManager)
- **Notre choix (Vérification au lancement) :** Les notifications de retard sont checkées et envoyées lorsque l'application passe au premier plan.
- **Pourquoi ?** Mettre en place un `WorkManager` ou un `AlarmManager` en arrière-plan (background) coûte cher en consommation de batterie, de mémoire, et nécessite une architecture lourde (BroadcastReceivers). Le compromis retenu ici garde l'app très légère ("Battery-friendly") tout en prévenant activement l'utilisateur quand il ouvre l'app ou son téléphone le matin.