package com.example.minniproj

import android.R.attr.contentDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.minniproj.model.ProfileScreen
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

        // ==========================================
        // MENU DE NAVIGATION
        // ==========================================

        navigationSuiteItems = {

            AppDestinations.entries.forEach { destination ->

                item(

                    // Icône
                    icon = {

                        Icon(
                            painter = painterResource(id = destination.icon),
                            contentDescription = destination.label,
                            modifier = Modifier.size(24.dp)
                        )



                    },

                    // Texte
                    label = {

                        Text(
                            text = destination.label
                        )
                    },

                    // Onglet sélectionné
                    selected =
                        destination == currentDestination,

                    // Action
                    onClick = {

                        currentDestination =
                            destination
                    }
                )
            }
        }
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            when (currentDestination) {

                // =========================================
                // HOME
                // =========================================

                AppDestinations.HOME -> {

                    HomeScreen(
                        modifier =
                            Modifier.padding(innerPadding),

                        onFavoritesClick = {

                            currentDestination =
                                AppDestinations.FAVORITES
                        }
                    )
                }


                // =========================================
                // FAVORIS
                // =========================================

                AppDestinations.FAVORITES -> {

                    FavoritesScreen(
                        onBack = {

                            currentDestination =
                                AppDestinations.HOME
                        }
                    )
                }


                // =========================================
                // PROFIL
                // =========================================

                AppDestinations.PROFILE -> {

                    ProfileScreen(
                        modifier =
                            Modifier.padding(innerPadding)
                    )
                }
            }
        }
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

    PROFILE(
        label = "Profil",
        icon = R.drawable.ic_account_box
    )
}