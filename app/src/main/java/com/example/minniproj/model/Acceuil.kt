package com.example.minniproj

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
// MODÈLES OPEN LIBRARY
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

    // Livres populaires
    @GET("people/mekBot/books/currently-reading.json")
    suspend fun getCurrentlyReading(): OpenLibraryResponse

    // Livres récemment ajoutés
    @GET("people/mekBot/books/want-to-read.json")
    suspend fun getWantToRead(): OpenLibraryResponse
}


// ======================================================
// RETROFIT
// ======================================================

object RetrofitInstance {

    private const val BASE_URL =
        "https://openlibrary.org/"

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

    // ==================================================
    // LIVRE SÉLECTIONNÉ
    // ==================================================

    var selectedBook by remember {
        mutableStateOf<Book?>(null)
    }


    // ==================================================
    // SI UN LIVRE EST SÉLECTIONNÉ
    // ==================================================

    if (selectedBook != null) {

        BookDetailScreen(

            book = selectedBook!!,

            onBack = {
                selectedBook = null
            }
        )

        return
    }


    // ==================================================
    // RECHERCHE
    // ==================================================

    var searchText by remember {
        mutableStateOf("")
    }


    // ==================================================
    // LIVRES POPULAIRES
    // ==================================================

    var popularBooks by remember {
        mutableStateOf<List<Book>>(emptyList())
    }


    // ==================================================
    // LIVRES RÉCENTS
    // ==================================================

    var recentBooks by remember {
        mutableStateOf<List<Book>>(emptyList())
    }


    // ==================================================
    // CHARGEMENT
    // ==================================================

    var isLoading by remember {
        mutableStateOf(true)
    }


    // ==================================================
    // ERREUR
    // ==================================================

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ==================================================
    // SCROLL VERTICAL
    // ==================================================

    val scrollState =
        rememberScrollState()


    // ==================================================
    // APPELS API
    // ==================================================

    LaunchedEffect(Unit) {

        try {

            // ==========================================
            // POPULAIRES
            // ==========================================

            val popularResponse =
                RetrofitInstance.api
                    .getCurrentlyReading()

            popularBooks =
                popularResponse.reading_log_entries
                    ?.mapNotNull { entry ->

                        val work = entry.work

                        if (work?.title == null) {

                            null

                        } else {

                            Book(

                                title =
                                    work.title,

                                author =
                                    work.author_names
                                        ?.joinToString(", ")
                                        ?: "Auteur inconnu",

                                year =
                                    work.first_publish_year
                                        ?.toString()
                                        ?: "Année inconnue",

                                coverId =
                                    work.cover_id
                            )
                        }
                    }
                    ?: emptyList()


            // ==========================================
            // RÉCENTS
            // ==========================================

            val recentResponse =
                RetrofitInstance.api
                    .getWantToRead()

            recentBooks =
                recentResponse.reading_log_entries
                    ?.mapNotNull { entry ->

                        val work = entry.work

                        if (work?.title == null) {

                            null

                        } else {

                            Book(

                                title =
                                    work.title,

                                author =
                                    work.author_names
                                        ?.joinToString(", ")
                                        ?: "Auteur inconnu",

                                year =
                                    work.first_publish_year
                                        ?.toString()
                                        ?: "Année inconnue",

                                coverId =
                                    work.cover_id
                            )
                        }
                    }
                    ?: emptyList()

        } catch (e: Exception) {

            errorMessage =
                e.message ?: "Erreur inconnue"

        } finally {

            isLoading = false
        }
    }


    // ==================================================
    // FILTRE POPULAIRES
    // ==================================================

    val filteredPopularBooks =
        popularBooks.filter { book ->

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
    // FILTRE RÉCENTS
    // ==================================================

    val filteredRecentBooks =
        recentBooks.filter { book ->

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
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {

        // ==================================================
        // BONJOUR
        // ==================================================

        Text(

            text = "Bonjour !",

            fontSize = 28.sp,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(5.dp)
        )


        Text(

            text =
                "Découvrez de nouveaux livres",

            fontSize =
                14.sp
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // ==================================================
        // RECHERCHE
        // ==================================================

        OutlinedTextField(

            value =
                searchText,

            onValueChange = {
                searchText = it
            },

            modifier =
                Modifier.fillMaxWidth(),

            placeholder = {

                Text(
                    text =
                        "Rechercher un livre..."
                )
            },

            singleLine = true
        )


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )


        // ==================================================
        // CHARGEMENT
        // ==================================================

        if (isLoading) {

            Column(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator()

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Chargement des livres..."
                )
            }


        } else if (errorMessage != null) {

            Text(

                text =
                    "Erreur lors du chargement des livres.",

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(
                text =
                    errorMessage ?: ""
            )


        } else {

            // ==================================================
            // LIVRES POPULAIRES
            // ==================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        "Livres populaires",

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        "Voir tout",

                    fontSize =
                        14.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )


            // ==================================================
            // LISTE HORIZONTALE
            // ==================================================

            LazyRow(

                horizontalArrangement =
                    Arrangement.spacedBy(15.dp)

            ) {

                items(
                    filteredPopularBooks
                ) { book ->

                    BookCard(

                        book = book,

                        onClick = {
                            selectedBook = book
                        }
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )


            // ==================================================
            // RÉCEMMENT AJOUTÉS
            // ==================================================

            Text(

                text =
                    "Récemment ajoutés",

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )


            // ==================================================
            // LISTE VERTICALE
            // ==================================================

            filteredRecentBooks.forEach { book ->

                RecentBook(

                    book = book,

                    onClick = {
                        selectedBook = book
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }
}


// ======================================================
// BOOK CARD
// ======================================================

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit
) {

    Column(

        modifier = Modifier
            .width(120.dp)
            .clickable {
                onClick()
            }
    ) {

        // ==================================================
        // COUVERTURE
        // ==================================================

        if (book.coverId != null) {

            AsyncImage(

                model =
                    "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",

                contentDescription =
                    "Couverture de ${book.title}",

                modifier =
                    Modifier
                        .size(
                            width = 120.dp,
                            height = 170.dp
                        )
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),

                contentScale =
                    ContentScale.Crop
            )

        } else {

            Column(

                modifier =
                    Modifier
                        .size(
                            width = 120.dp,
                            height = 170.dp
                        )
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            Color.LightGray
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "📖",
                    fontSize = 35.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Text(

            text =
                book.title,

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Bold,

            maxLines =
                2
        )


        Text(

            text =
                book.author,

            fontSize =
                12.sp,

            maxLines =
                1
        )


        Text(

            text =
                book.year,

            fontSize =
                11.sp
        )
    }
}


// ======================================================
// RECENT BOOK
// ======================================================

@Composable
fun RecentBook(
    book: Book,
    onClick: () -> Unit
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ==================================================
        // COUVERTURE
        // ==================================================

        if (book.coverId != null) {

            AsyncImage(

                model =
                    "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",

                contentDescription =
                    "Couverture de ${book.title}",

                modifier =
                    Modifier
                        .size(
                            width = 60.dp,
                            height = 80.dp
                        )
                        .clip(
                            RoundedCornerShape(8.dp)
                        ),

                contentScale =
                    ContentScale.Crop
            )

        } else {

            Column(

                modifier =
                    Modifier
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

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "📖",
                    fontSize = 25.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(15.dp)
        )


        // ==================================================
        // INFORMATIONS
        // ==================================================

        Column {

            Text(

                text =
                    book.title,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Bold,

                maxLines =
                    2
            )


            Text(

                text =
                    book.author,

                fontSize =
                    13.sp,

                maxLines =
                    1
            )


            Text(

                text =
                    book.year,

                fontSize =
                    12.sp
            )
        }
    }
}


// ======================================================
// PAGE DÉTAILS DU LIVRE
// ======================================================

@Composable
fun BookDetailScreen(
    book: Book,
    onBack: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp)
    ) {

        // ==================================================
        // RETOUR
        // ==================================================

        Button(
            onClick = onBack
        ) {

            Text(
                text = "Retour"
            )
        }


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // ==================================================
        // COUVERTURE
        // ==================================================

        if (book.coverId != null) {

            AsyncImage(

                model =
                    "https://covers.openlibrary.org/b/id/${book.coverId}-L.jpg",

                contentDescription =
                    "Couverture de ${book.title}",

                modifier =
                    Modifier
                        .size(
                            width = 220.dp,
                            height = 320.dp
                        )
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .align(
                            Alignment.CenterHorizontally
                        ),

                contentScale =
                    ContentScale.Crop
            )

        } else {

            Column(

                modifier =
                    Modifier
                        .size(
                            width = 220.dp,
                            height = 320.dp
                        )
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            Color.LightGray
                        )
                        .align(
                            Alignment.CenterHorizontally
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "📖",
                    fontSize = 60.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // ==================================================
        // TITRE
        // ==================================================

        Text(

            text =
                book.title,

            fontSize =
                26.sp,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ==================================================
        // AUTEUR
        // ==================================================

        Text(

            text =
                "Auteur",

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.Gray
        )


        Text(

            text =
                book.author,

            fontSize =
                18.sp
        )


        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        // ==================================================
        // ANNÉE
        // ==================================================

        Text(

            text =
                "Année de publication",

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                Color.Gray
        )


        Text(

            text =
                book.year,

            fontSize =
                18.sp
        )


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )


        // ==================================================
        // DESCRIPTION
        // ==================================================

        Text(

            text =
                "Informations sur le livre",

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Text(

            text =
                "Ce livre est actuellement disponible dans votre bibliothèque Open Library.",

            fontSize =
                15.sp,

            lineHeight =
                22.sp,

            color =
                Color.Gray
        )
    }
}
