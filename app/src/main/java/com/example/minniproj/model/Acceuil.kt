package com.example.minniproj
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


// ======================================================
// MODÈLE LIVRE
// ======================================================

data class Book(
    val title: String,
    val author: String,
    val year: String,
    val coverId: Int?
)


// ======================================================
// MODÈLES DE L'API OPEN LIBRARY
// ======================================================

data class OpenLibraryResponse(
    val reading_log_entries: List<ReadingLogEntry>?
)

data class ReadingLogEntry(
    val work: Work?
)

data class Work(
    val title: String?,
    val author_names: List<String>?,
    val first_publish_year: Int?,
    val cover_id: Int?
)


// ======================================================
// API RETROFIT
// ======================================================

interface OpenLibraryApi {

    @GET("people/mekBot/books/want-to-read.json")
    suspend fun getWantToRead(): OpenLibraryResponse
}


// ======================================================
// RETROFIT
// ======================================================

object RetrofitInstance {

    private const val BASE_URL = "https://openlibrary.org/"

    val api: OpenLibraryApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(OpenLibraryApi::class.java)
    }
}


// ======================================================
// HOME SCREEN
// ======================================================

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var books by remember {
        mutableStateOf<List<Book>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ==================================================
    // APPEL API
    // ==================================================

    LaunchedEffect(Unit) {

        try {

            val response = RetrofitInstance.api.getWantToRead()

            books = response.reading_log_entries
                ?.mapNotNull { entry ->

                    val work = entry.work

                    if (work?.title == null) {
                        null
                    } else {

                        Book(
                            title = work.title,
                            author = work.author_names
                                ?.joinToString(", ")
                                ?: "Auteur inconnu",
                            year = work.first_publish_year
                                ?.toString()
                                ?: "Année inconnue",
                            coverId = work.cover_id
                        )
                    }
                }
                ?: emptyList()

        } catch (e: Exception) {

            errorMessage = e.message

        } finally {

            isLoading = false
        }
    }


    // ==================================================
    // FILTRAGE RECHERCHE
    // ==================================================

    val filteredBooks = books.filter { book ->

        book.title.contains(
            searchText,
            ignoreCase = true
        ) ||
                book.author.contains(
                    searchText,
                    ignoreCase = true
                )
    }


    // ==================================================
    // INTERFACE
    // ==================================================

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // ----------------------------------------------
        // Bonjour
        // ----------------------------------------------

        Text(
            text = "Bonjour !",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Découvrez de nouveaux livres",
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // ----------------------------------------------
        // Recherche
        // ----------------------------------------------

        OutlinedTextField(
            value = searchText,

            onValueChange = {
                searchText = it
            },

            modifier = Modifier.fillMaxWidth(),

            placeholder = {
                Text("Rechercher un livre...")
            },

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // ----------------------------------------------
        // CHARGEMENT
        // ----------------------------------------------

        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Chargement des livres..."
                )
            }

        }


        // ----------------------------------------------
        // ERREUR
        // ----------------------------------------------

        else if (errorMessage != null) {

            Text(
                text = "Erreur lors du chargement des livres.",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = errorMessage ?: ""
            )
        }


        // ----------------------------------------------
        // LIVRES
        // ----------------------------------------------

        else {

            // ==========================================
            // LIVRES POPULAIRES
            // ==========================================

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Livres populaires",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Voir tout",
                    fontSize = 14.sp
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )


            // ==========================================
            // LISTE HORIZONTALE
            // ==========================================

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(15.dp)
            ) {

                items(filteredBooks) { book ->

                    BookCard(book)
                }
            }


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // ==========================================
            // RÉCEMMENT AJOUTÉS
            // ==========================================

            Text(
                text = "Récemment ajoutés",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )


            filteredBooks.forEach { book ->

                RecentBook(book)

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}


// ======================================================
// CARTE LIVRE
// ======================================================

@Composable
fun BookCard(
    book: Book
) {

    Column(
        modifier = Modifier.width(120.dp)
    ) {

        if (book.coverId != null) {

            AsyncImage(
                model = "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",
                contentDescription = "Couverture de ${book.title}",
                modifier = Modifier
                    .size(
                        width = 120.dp,
                        height = 170.dp
                    )
                    .clip(
                        RoundedCornerShape(10.dp)
                    ),
                contentScale = ContentScale.Crop
            )

        } else {

            Column(
                modifier = Modifier
                    .size(
                        width = 120.dp,
                        height = 170.dp
                    )
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(Color.LightGray),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "📖",
                    fontSize = 35.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = book.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )

        Text(
            text = book.author,
            fontSize = 12.sp,
            maxLines = 1
        )

        Text(
            text = book.year,
            fontSize = 11.sp
        )
    }
}
@Composable
fun RecentBook(
    book: Book
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==========================================
        // COUVERTURE DU LIVRE
        // ==========================================

        if (book.coverId != null) {

            AsyncImage(
                model = "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",

                contentDescription = "Couverture de ${book.title}",

                modifier = Modifier
                    .size(
                        width = 60.dp,
                        height = 80.dp
                    )
                    .clip(
                        RoundedCornerShape(8.dp)
                    ),

                contentScale = ContentScale.Crop
            )

        } else {

            // Si aucune couverture n'est disponible
            Column(
                modifier = Modifier
                    .size(
                        width = 60.dp,
                        height = 80.dp
                    )
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(
                        Color.LightGray
                    ),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "📖",
                    fontSize = 25.sp
                )
            }
        }


        // ==========================================
        // ESPACE ENTRE IMAGE ET TEXTE
        // ==========================================

        Spacer(
            modifier = Modifier.width(15.dp)
        )


        // ==========================================
        // INFORMATIONS DU LIVRE
        // ==========================================

        Column {

            Text(
                text = book.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = book.author,
                fontSize = 13.sp,
                maxLines = 1
            )

            Text(
                text = book.year,
                fontSize = 12.sp
            )
        }
    }
}