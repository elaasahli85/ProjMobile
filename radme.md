# 📚 MinniProj – Bibliothèque Open Library

Application Android de découverte de livres et d'auteurs, développée en **Kotlin** avec **Jetpack Compose**. Elle s'appuie sur l'API publique [Open Library](https://openlibrary.org/) et permet de sauvegarder ses livres préférés en local.

---

## ✨ Fonctionnalités

### 🏠 Accueil
- Salutation selon l'heure (Bonjour / Bonsoir)
- Barre de recherche (titre ou auteur) avec effacement rapide
- Filtres : **Tout**, **Populaires**, **Récents**
- Statistiques animées (nombre de livres, d'auteurs, tendances)
- Carte **« À la une »** avec couverture animée
- Citations littéraires qui défilent toutes les 5 secondes
- Section **« Sur l'étagère »** : livres posés sur une étagère en bois (classement #1, #2…)
- Section **« Récemment ajoutés »** avec badge « NOUVEAU »

### 📖 Détail d'un livre
- Grande couverture avec effet de parallaxe et halo lumineux
- Infos : auteur (cliquable), année de publication
- Bouton **Ajouter / Retirer des favoris** avec animation d'explosion de cœurs

### ✍️ Auteurs
- Recherche d'auteurs avec *debounce* (400 ms) et recherche insensible aux accents
- Suggestions (Victor Hugo, Agatha Christie, Albert Camus, Jules Verne, etc.)
- Fiche auteur : photo (ou initiales), dates, œuvre principale, nombre d'œuvres
- Liste de tous les livres d'un auteur

### ❤️ Favoris
- Liste des livres favoris stockés localement (Room)
- Suppression avec animation (glissement + disparition)
- Écran vide illustré quand il n'y a aucun favori

### 🎨 Interface
- Palette « bibliothèque vive » (violet, corail, ambre, menthe, fond crème)
- Animations partout : entrées décalées, rebond au toucher, shimmer de chargement, transitions de navigation
- Couvertures de secours (dégradé coloré + titre) quand aucune image n'est disponible
- Gestion des états **chargement / erreur (avec bouton Réessayer) / vide**

---

## 🛠️ Stack technique

| Domaine | Technologie |
|---|---|
| Langage | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Réseau | Retrofit 2 + Gson |
| Images | Coil (`AsyncImage`) |
| Base de données locale | Room |
| Concurrence | Coroutines (`async`, `awaitAll`, `LaunchedEffect`) |
| API | Open Library |

---

## 🌐 Endpoints Open Library utilisés

| Usage | Endpoint |
|---|---|
| Livres en cours de lecture (populaires) | `GET /people/mekBot/books/currently-reading.json` |
| Livres à lire (récents) | `GET /people/mekBot/books/want-to-read.json` |
| Livres d'un auteur (par nom) | `GET /search.json?author=…` |
| Recherche d'auteurs | `GET /search/authors.json?q=…` |
| Livres d'un auteur (par identifiant) | `GET /search.json?author_key=…` |
| Couvertures de livres | `https://covers.openlibrary.org/b/id/{coverId}-{S\|M\|L}.jpg` |
| Photos d'auteurs | `https://covers.openlibrary.org/a/olid/{olid}-M.jpg` |

Aucune clé d'API n'est nécessaire.

---

## 🗂️ Structure du projet

```
com.example.minniproj
├── AppDatabase                 # Base Room (accès via AppDatabase.getDatabase(context))
├── AuthorsScreen               # Recherche d'auteurs + livres d'un auteur (AuthorWorksScreen)
├── FavoritesScreen             # Écran des favoris
└── model
    ├── HomeScreen              # Accueil, détail livre, livres d'un auteur, composants communs
    └── FavoriteEntity          # Entité Room (table « favorites »)
```

### Modèle de données

```kotlin
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val coverId: Int,
    val title: String,
    val author: String,
    val year: String
)
```

> ⚠️ Les favoris sont identifiés par `coverId` : un livre sans couverture ne peut donc pas être ajouté aux favoris (le bouton est masqué dans ce cas).

---

## 🚀 Installation

### Prérequis
- Android Studio (version récente)
- JDK 17
- Un appareil ou émulateur Android avec accès Internet

### Étapes
1. Cloner le dépôt :
   ```bash
   git clone <url-du-depot>
   ```
2. Ouvrir le projet dans Android Studio.
3. Laisser Gradle synchroniser les dépendances.
4. Lancer l'application (▶️ Run).

### Permission requise
Dans `AndroidManifest.xml` :
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Dépendances principales (Gradle)
```kotlin
// Retrofit + Gson
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Coil
implementation("io.coil-kt:coil-compose:2.6.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Compose
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")
```
*(Adaptez les versions à votre projet.)*

---

## 🧭 Navigation

```
Accueil ──► Détail du livre ──► Livres de l'auteur ──► Détail du livre
   │
   └──► Livres de l'auteur

Onglet Auteurs ──► Livres de l'auteur (AuthorWorksScreen)
Favoris ──► (retour)
```

Le bouton retour système est géré via `BackHandler`, avec des transitions animées (glissement + fondu) selon la profondeur de navigation.

---

## 🔮 Pistes d'amélioration
- Migrer vers une architecture **MVVM** (ViewModel + StateFlow) et Hilt
- Mise en cache hors-ligne des listes
- Mode sombre

---

## 📄 Licence

Projet réalisé à but pédagogique. Les données et couvertures proviennent d'[Open Library](https://openlibrary.org/) (Internet Archive).
