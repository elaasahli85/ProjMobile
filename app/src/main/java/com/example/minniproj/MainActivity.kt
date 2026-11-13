package com.example.minniproj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.minniproj.model.Book
import com.example.minniproj.model.BookDetailScreen
import com.example.minniproj.model.HomeScreen
import com.example.minniproj.ui.theme.MinniProjTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MinniProjTheme {
                MinniProjApp()
            }
        }
    }
}


@PreviewScreenSizes
@Composable
fun MinniProjApp() {

    var currentDestination by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }

    NavigationSuiteScaffold(

        navigationSuiteItems = {

            AppDestinations.entries.forEach { destination ->

                item(
                    icon = {
                        Icon(
                            painter = painterResource(id = destination.icon),
                            contentDescription = destination.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },

                    label = {
                        Text(text = destination.label)
                    },

                    selected = destination == currentDestination,

                    onClick = {
                        currentDestination = destination
                    }
                )
            }
        }
    ) {

        // Pas de padding système ici : les en-têtes dégradés des écrans
        // gèrent eux-mêmes la barre d'état (statusBarsPadding)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { _ ->

            when (currentDestination) {

                AppDestinations.HOME -> HomeScreen()

                AppDestinations.FAVORITES -> FavoritesScreen(
                    onBack = {
                        currentDestination = AppDestinations.HOME
                    }
                )

                AppDestinations.AUTHORS -> AuthorsTab()
            }
        }
    }
}


// ======================================================
// ONGLET AUTEURS : recherche -> livres de l'auteur -> détail
// ======================================================

@Composable
fun AuthorsTab() {

    // L'auteur est identifié par son ID Open Library (pas seulement son nom)
    var pickedAuthorKey by rememberSaveable { mutableStateOf<String?>(null) }
    var pickedAuthorName by rememberSaveable { mutableStateOf("") }
    var pickedBook by remember { mutableStateOf<Book?>(null) }

    // Bouton retour du téléphone : détail -> livres de l'auteur -> recherche
    BackHandler(enabled = pickedBook != null || pickedAuthorKey != null) {
        if (pickedBook != null) {
            pickedBook = null
        } else {
            pickedAuthorKey = null
        }
    }

    val book = pickedBook
    val authorKey = pickedAuthorKey

    when {

        book != null -> BookDetailScreen(
            book = book,
            onBack = { pickedBook = null },
            // On est déjà dans l'onglet de cet auteur : on revient à sa liste
            onAuthorClick = { pickedBook = null }
        )

        authorKey != null -> AuthorWorksScreen(
            authorKey = authorKey,
            authorName = pickedAuthorName,
            onBack = { pickedAuthorKey = null },
            onBookClick = { pickedBook = it }
        )

        else -> AuthorsScreen(
            onAuthorClick = { key, name ->
                pickedAuthorName = name
                pickedAuthorKey = key
            }
        )
    }
}


// ======================================================
// DESTINATIONS
// ======================================================

enum class AppDestinations(

    val label: String,

    val icon: Int

) {

    HOME(
        label = "Accueil",
        icon = R.drawable.img
    ),

    FAVORITES(
        label = "Favoris",
        icon = R.drawable.img_1
    ),

    AUTHORS(
        label = "Auteurs",
        icon = R.drawable.ic_account_box
    )
}