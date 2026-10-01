package com.example.minniproj

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.minniproj.model.FavoriteEntity
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onBack: () -> Unit
) {

    // ============================================
    // DATABASE
    // ============================================

    val context = androidx.compose.ui.platform.LocalContext.current

    val database = remember {
        AppDatabase.getDatabase(context)
    }

    val dao = database.favoriteDao()

    // ============================================
    // COROUTINE
    // ============================================

    val scope = rememberCoroutineScope()

    // ============================================
    // LISTE DES FAVORIS
    // ============================================

    var favorites by remember {
        mutableStateOf<List<FavoriteEntity>>(emptyList())
    }

    // ============================================
    // CHARGEMENT
    // ============================================

    LaunchedEffect(Unit) {
        favorites = dao.getAllFavorites()
    }

    // ============================================
    // INTERFACE
    // ============================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(20.dp)
    ) {

        // ========================================
        // HEADER
        // ========================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {


        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favoris",
                fontSize = 22.sp,
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.m),
                contentDescription = "Favoris",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )


        }
        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "${favorites.size} livre(s) enregistré(s)",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // ========================================
        // AUCUN FAVORI
        // ========================================

        if (favorites.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "📚",
                    fontSize = 55.sp
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = "Aucun favori",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Ajoutez des livres à vos favoris\npour les retrouver ici.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            // ====================================
            // LISTE
            // ====================================

            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(15.dp)
            ) {

                items(
                    items = favorites,
                    key = { favorite ->
                        favorite.coverId
                    }
                ) { favorite ->

                    FavoriteItem(
                        favorite = favorite,

                        onDelete = {

                            scope.launch {

                                dao.deleteFavorite(favorite)

                                favorites =
                                    dao.getAllFavorites()
                            }
                        }
                    )
                }
            }
        }
    }
}


// ======================================================
// ITEM FAVORI
// ======================================================

@Composable
fun FavoriteItem(
    favorite: FavoriteEntity,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = "https://covers.openlibrary.org/b/id/${favorite.coverId}-M.jpg",
            contentDescription = "Couverture de ${favorite.title}",
            modifier = Modifier.size(
                width = 70.dp,
                height = 100.dp
            ),
            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.width(15.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = favorite.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = favorite.author,
                fontSize = 13.sp
            )

            Text(
                text = favorite.year,
                fontSize = 12.sp
            )
        }

        // 🗑️ Bouton poubelle
        // Supprimer
        Text(
            text = "🗑️",
            fontSize = 22.sp,
            modifier = Modifier.clickable {
                onDelete()
            }
        )
        }
    }
