package com.example.minniproj.model

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.minniproj.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Calendar


// ======================================================
// COULEURS (palette bibliothèque vive)
// ======================================================

private val AppBackground = Color(0xFFFBF7F1)      // papier crème
private val PrimaryPurple = Color(0xFF5B3FC4)
private val DeepPurple = Color(0xFF2E1C80)
private val SoftPurple = Color(0xFF9378F5)
private val LightPurple = Color(0xFFEDE8FF)
private val Coral = Color(0xFFFF6B6B)
private val Amber = Color(0xFFFFB347)
private val Mint = Color(0xFF00B894)
private val DarkText = Color(0xFF211F36)
private val GrayText = Color(0xFF74728A)
private val LightGrayText = Color(0xFF9996A8)
private val FavoriteRed = Color(0xFFFF4D6A)
private val QuoteBackground = Color(0xFFFFF1D6)

private val HeaderBrush = Brush.linearGradient(
    colors = listOf(DeepPurple, PrimaryPurple, SoftPurple)
)

private val ShelfBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFC08A52), Color(0xFF8B5A2B), Color(0xFF5E3A1C))
)

// Dégradés pour les couvertures sans image : une couleur stable par titre
private val CoverGradients = listOf(
    listOf(Color(0xFF7F5AF0), Color(0xFF5B3FC4)),
    listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)),
    listOf(Color(0xFF00B894), Color(0xFF0984E3)),
    listOf(Color(0xFFF6A04D), Color(0xFFF6D365)),
    listOf(Color(0xFFE84393), Color(0xFF6C5CE7)),
    listOf(Color(0xFF2D3A8C), Color(0xFF4FACFE))
)

private val Quotes = listOf(
    "Un livre est un jardin que l'on porte dans sa poche." to "Proverbe arabe",
    "Lire, c'est voyager sans quitter son fauteuil." to "Une idée de bibliothécaire",
    "Chaque livre ouvert est une porte vers un autre monde." to "Une idée de bibliothécaire",
    "Une bibliothèque est un trésor qui ne s'épuise jamais." to "Une idée de bibliothécaire"
)


// ======================================================
// MODÈLES
// ======================================================

data class Book(
    val title: String,
    val author: String,
    val year: String,
    val coverId: Int?
)

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

data class AuthorSearchResponse(
    val docs: List<AuthorBookDoc>?
)

data class AuthorBookDoc(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val cover_i: Int?
)


// ======================================================
// API / RETROFIT
// ======================================================

interface OpenLibraryApi {

    @GET("people/mekBot/books/currently-reading.json")
    suspend fun getCurrentlyReading(): OpenLibraryResponse

    @GET("people/mekBot/books/want-to-read.json")
    suspend fun getWantToRead(): OpenLibraryResponse

    @GET("search.json")
    suspend fun searchBooksByAuthor(
        @Query("author") author: String,
        @Query("limit") limit: Int = 50
    ): AuthorSearchResponse
}

object RetrofitInstance {

    private const val BASE_URL = "https://openlibrary.org/"

    val api: OpenLibraryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApi::class.java)
    }
}

private fun OpenLibraryResponse.toBooks(): List<Book> =
    reading_log_entries
        ?.mapNotNull { entry ->
            val work = entry.work
            val title = work?.title ?: return@mapNotNull null
            Book(
                title = title,
                author = work.author_names?.joinToString(", ") ?: "Auteur inconnu",
                year = work.first_publish_year?.toString() ?: "Année inconnue",
                coverId = work.cover_id
            )
        }
        ?: emptyList()


// ======================================================
// HELPERS D'ANIMATION
// ======================================================

/** Rebond quand on appuie sur un élément cliquable. */
@Composable
private fun Modifier.pressScale(
    pressedScale: Float = 0.96f,
    onClick: () -> Unit
): Modifier {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressScale"
    )
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            onClick = onClick
        )
}

/** Apparition en fondu + glissement, décalée selon l'index (jouée une seule fois). */
@Composable
private fun Modifier.entrance(
    index: Int,
    offsetY: Dp = 28.dp
): Modifier {
    var played by rememberSaveable { mutableStateOf(false) }
    val progress = remember { Animatable(if (played) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!played) {
            delay((index * 70L).coerceAtMost(450L))
            progress.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            played = true
        }
    }

    val offsetPx = with(LocalDensity.current) { offsetY.toPx() }

    return this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * offsetPx
    }
}

/** Fond dégradé avec bulles qui flottent. */
@Composable
private fun Modifier.headerBackground(corner: Dp = 32.dp): Modifier {
    val transition = rememberInfiniteTransition(label = "header")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    return this
        .clip(RoundedCornerShape(bottomStart = corner, bottomEnd = corner))
        .background(HeaderBrush)
        .drawBehind {
            drawCircle(
                color = Color.White.copy(alpha = 0.09f),
                radius = size.width * 0.34f,
                center = Offset(
                    x = size.width * (0.92f - 0.06f * drift),
                    y = size.height * (0.05f + 0.15f * drift)
                )
            )
            drawCircle(
                color = Coral.copy(alpha = 0.22f),
                radius = size.width * 0.16f,
                center = Offset(
                    x = size.width * (0.55f + 0.08f * drift),
                    y = size.height * (0.9f - 0.2f * drift)
                )
            )
            drawCircle(
                color = Amber.copy(alpha = 0.20f),
                radius = size.width * 0.10f,
                center = Offset(
                    x = size.width * (0.08f + 0.07f * drift),
                    y = size.height * (0.30f + 0.2f * drift)
                )
            )
        }
}

/** Pinceau "shimmer" pour les squelettes. */
@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing)
        ),
        label = "shimmerX"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFEBE4D8),
            Color(0xFFFAF6EF),
            Color(0xFFEBE4D8)
        ),
        start = Offset(x - 400f, 0f),
        end = Offset(x, 300f)
    )
}

private fun depthOf(state: Pair<Book?, String?>): Int =
    if (state.first == null && state.second == null) 0 else 1


// ======================================================
// HOME SCREEN (navigation + chargement)
// ======================================================

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {

    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var selectedAuthor by remember { mutableStateOf<String?>(null) }

    var searchText by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(0) } // 0 = tout, 1 = populaires, 2 = récents
    var popularBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var recentBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    val homeScroll = rememberScrollState()

    LaunchedEffect(reloadKey) {
        isLoading = true
        errorMessage = null
        try {
            popularBooks = RetrofitInstance.api.getCurrentlyReading().toBooks()
            recentBooks = RetrofitInstance.api.getWantToRead().toBooks()
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erreur inconnue"
        } finally {
            isLoading = false
        }
    }

    BackHandler(enabled = selectedBook != null || selectedAuthor != null) {
        selectedBook = null
        selectedAuthor = null
    }

    AnimatedContent(
        targetState = Pair(selectedBook, selectedAuthor),
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground),
        transitionSpec = {
            val deeper = depthOf(targetState) > depthOf(initialState)
            val shallower = depthOf(targetState) < depthOf(initialState)
            when {
                deeper ->
                    (slideInHorizontally(tween(380, easing = FastOutSlowInEasing)) { it / 3 } +
                            fadeIn(tween(380))) togetherWith
                            (slideOutHorizontally(tween(380, easing = FastOutSlowInEasing)) { -it / 6 } +
                                    fadeOut(tween(220)))

                shallower ->
                    (slideInHorizontally(tween(380, easing = FastOutSlowInEasing)) { -it / 6 } +
                            fadeIn(tween(380))) togetherWith
                            (slideOutHorizontally(tween(380, easing = FastOutSlowInEasing)) { it / 3 } +
                                    fadeOut(tween(220)))

                else -> fadeIn(tween(320)) togetherWith fadeOut(tween(200))
            }
        },
        label = "navigation"
    ) { (book, author) ->

        when {
            author != null -> AuthorBooksScreen(
                author = author,
                onBack = { selectedAuthor = null },
                onBookClick = {
                    selectedAuthor = null
                    selectedBook = it
                }
            )

            book != null -> BookDetailScreen(
                book = book,
                onBack = { selectedBook = null },
                onAuthorClick = {
                    selectedAuthor = book.author
                    selectedBook = null
                }
            )

            else -> HomeContent(
                scrollState = homeScroll,
                searchText = searchText,
                onSearchChange = { searchText = it },
                filter = filter,
                onFilterChange = { filter = it },
                popularBooks = popularBooks,
                recentBooks = recentBooks,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onRetry = { reloadKey++ },
                onBookClick = { selectedBook = it },
                onAuthorClick = { selectedAuthor = it }
            )
        }
    }
}


// ======================================================
// CONTENU DE L'ACCUEIL
// ======================================================

@Composable
private fun HomeContent(
    scrollState: ScrollState,
    searchText: String,
    onSearchChange: (String) -> Unit,
    filter: Int,
    onFilterChange: (Int) -> Unit,
    popularBooks: List<Book>,
    recentBooks: List<Book>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onBookClick: (Book) -> Unit,
    onAuthorClick: (String) -> Unit
) {

    val filteredPopular = popularBooks.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.author.contains(searchText, ignoreCase = true)
    }

    val filteredRecent = recentBooks.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.author.contains(searchText, ignoreCase = true)
    }

    val showPopular = filter != 2 && filteredPopular.isNotEmpty()
    val showRecent = filter != 1 && filteredRecent.isNotEmpty()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour in 5..17) "Bonjour" else "Bonsoir"
    }

    val totalBooks = (popularBooks + recentBooks).distinctBy { it.title }.size
    val totalAuthors = (popularBooks + recentBooks).map { it.author }.distinct().size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
    ) {

        // ================= HEADER =================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .headerBackground(36.dp)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(0, 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$greeting 👋",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Bienvenue dans votre bibliothèque",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                BobbingBooks()
            }

            Spacer(Modifier.height(22.dp))

            Box(modifier = Modifier.entrance(1, 16.dp)) {
                SearchBar(
                    value = searchText,
                    onValueChange = onSearchChange
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ================= FILTRES =================

        FilterChips(
            selected = filter,
            onSelect = onFilterChange,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .entrance(2, 12.dp)
        )

        Spacer(Modifier.height(20.dp))

        when {
            isLoading -> HomeSkeleton()

            errorMessage != null -> ErrorView(
                message = errorMessage,
                onRetry = onRetry
            )

            !showPopular && !showRecent -> EmptyView(
                text = if (searchText.isBlank())
                    "Aucun livre à afficher pour le moment."
                else
                    "Aucun résultat pour « $searchText »"
            )

            else -> {

                // ================= STATS =================

                if (searchText.isBlank() && filter == 0) {

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .entrance(3, 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard("📚", totalBooks, "Livres", Coral, Modifier.weight(1f))
                        StatCard("✍️", totalAuthors, "Auteurs", Mint, Modifier.weight(1f))
                        StatCard("🔥", filteredPopular.size, "Tendances", Amber, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(22.dp))

                    // ================= À LA UNE =================

                    if (filteredPopular.isNotEmpty()) {
                        FeaturedBook(
                            book = filteredPopular.first(),
                            onClick = { onBookClick(filteredPopular.first()) },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .entrance(4, 28.dp)
                        )

                        Spacer(Modifier.height(22.dp))
                    }

                    QuoteCard(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .entrance(5, 20.dp)
                    )

                    Spacer(Modifier.height(28.dp))
                }

                // ================= ÉTAGÈRE POPULAIRES =================

                if (showPopular) {

                    SectionHeader(
                        title = "Sur l'étagère",
                        emoji = "🔥",
                        count = filteredPopular.size,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .entrance(6, 16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        itemsIndexed(filteredPopular) { index, book ->
                            ModernBookCard(
                                book = book,
                                rank = index + 1,
                                onClick = { onBookClick(book) },
                                onAuthorClick = { onAuthorClick(book.author) },
                                modifier = Modifier.entrance(index + 3, 32.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(30.dp))
                }

                // ================= RÉCENTS =================

                if (showRecent) {

                    SectionHeader(
                        title = "Récemment ajoutés",
                        emoji = "🆕",
                        count = filteredRecent.size,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .entrance(6, 16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        filteredRecent.forEachIndexed { index, book ->
                            ModernRecentBook(
                                book = book,
                                isNew = index < 3,
                                onClick = { onBookClick(book) },
                                onAuthorClick = { onAuthorClick(book.author) },
                                modifier = Modifier.entrance(index + 5, 32.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}


// ======================================================
// PETITS COMPOSANTS VIVANTS
// ======================================================

/** Icône 📚 qui flotte doucement dans l'en-tête. */
@Composable
private fun BobbingBooks() {
    val transition = rememberInfiniteTransition(label = "bob")
    val bob by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobY"
    )
    val tilt by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobTilt"
    )

    Surface(
        modifier = Modifier.size(54.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "📚",
                fontSize = 28.sp,
                modifier = Modifier.graphicsLayer {
                    translationY = bob
                    rotationZ = tilt
                }
            )
        }
    }
}

@Composable
private fun FilterChips(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = listOf("✨ Tout", "🔥 Populaires", "🆕 Récents")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        labels.forEachIndexed { index, label ->

            val isSelected = selected == index

            val background by animateColorAsState(
                targetValue = if (isSelected) PrimaryPurple else Color.White,
                animationSpec = tween(300),
                label = "chipBg"
            )
            val content by animateColorAsState(
                targetValue = if (isSelected) Color.White else DarkText,
                animationSpec = tween(300),
                label = "chipFg"
            )
            val elevation by animateDpAsState(
                targetValue = if (isSelected) 8.dp else 2.dp,
                label = "chipElevation"
            )

            Surface(
                modifier = Modifier.pressScale(0.92f) { onSelect(index) },
                shape = RoundedCornerShape(50),
                color = background,
                shadowElevation = elevation
            ) {
                Text(
                    text = label,
                    color = content,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: Int,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val animated by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "stat"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 19.sp)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$animated",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = GrayText
            )
        }
    }
}

/** Grande carte "À la une" avec couverture qui se balance. */
@Composable
private fun FeaturedBook(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "featured")
    val sway by transition.animateFloat(
        initialValue = -9f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(0.97f, onClick),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(listOf(Coral, Color(0xFFB04BD8), PrimaryPurple))
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.22f)
                ) {
                    Text(
                        text = "⭐ À LA UNE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = book.title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 26.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = book.author,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White
                ) {
                    Text(
                        text = "Découvrir  ›",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            BookCover(
                coverId = book.coverId,
                title = book.title,
                modifier = Modifier
                    .size(width = 104.dp, height = 152.dp)
                    .graphicsLayer { rotationZ = sway }
                    .shadow(16.dp, RoundedCornerShape(12.dp)),
                corner = 12.dp,
                emojiSize = 28
            )
        }
    }
}

/** Citation qui change toutes les 5 secondes. */
@Composable
private fun QuoteCard(modifier: Modifier = Modifier) {

    var index by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            index = (index + 1) % Quotes.size
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = QuoteBackground
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {

            Text(text = "“", fontSize = 44.sp, color = Amber, lineHeight = 40.sp)

            Spacer(Modifier.width(10.dp))

            AnimatedContent(
                targetState = index,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
                label = "quote"
            ) { i ->
                Column {
                    Text(
                        text = Quotes[i].first,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        color = DarkText,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = Quotes[i].second,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFB07A1F)
                    )
                }
            }
        }
    }
}


// ======================================================
// COUVERTURE (fausse couverture colorée si pas d'image)
// ======================================================

@Composable
private fun BookCover(
    coverId: Int?,
    title: String,
    modifier: Modifier = Modifier,
    corner: Dp = 14.dp,
    sizeCode: String = "M",
    emojiSize: Int = 30
) {
    val context = LocalContext.current
    val gradient = CoverGradients[(title.hashCode() and Int.MAX_VALUE) % CoverGradients.size]

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {

        // Dos du livre
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width((emojiSize / 5).coerceAtLeast(4).dp)
                .background(Color.Black.copy(alpha = 0.18f))
        )

        // Titre affiché pendant le chargement / sans image
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (emojiSize * 0.42f).sp,
            lineHeight = (emojiSize * 0.52f).sp,
            textAlign = TextAlign.Center,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        if (coverId != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("https://covers.openlibrary.org/b/id/$coverId-$sizeCode.jpg")
                    .crossfade(450)
                    .build(),
                contentDescription = "Couverture de $title",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


// ======================================================
// BARRE DE RECHERCHE
// ======================================================

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {

    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()

    val elevation by animateDpAsState(
        targetValue = if (focused) 14.dp else 4.dp,
        label = "searchElevation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (focused) Amber else Color.Transparent,
        label = "searchBorder"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (focused) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "searchIcon"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = elevation,
        border = BorderStroke(2.dp, borderColor)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher",
                tint = PrimaryPurple,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )

            Spacer(Modifier.width(10.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                interactionSource = interaction,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 15.sp, color = DarkText),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = "Rechercher un livre, un auteur...",
                                color = Color(0xFFAAA8B7),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            AnimatedVisibility(
                visible = value.isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onValueChange("") },
                    shape = CircleShape,
                    color = LightPurple
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Effacer",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}


// ======================================================
// HEADER SECTION
// ======================================================

@Composable
fun SectionHeader(
    title: String,
    emoji: String,
    count: Int,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(text = emoji, fontSize = 20.sp)

            Spacer(Modifier.width(8.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )
        }

        Surface(
            shape = RoundedCornerShape(50),
            color = LightPurple
        ) {
            Text(
                text = "$count",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}


// ======================================================
// SQUELETTES DE CHARGEMENT
// ======================================================

@Composable
private fun HomeSkeleton() {

    val brush = shimmerBrush()

    Column {

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(brush)
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(brush)
        )

        Spacer(Modifier.height(28.dp))

        SkeletonList(count = 3)
    }
}

@Composable
private fun SkeletonList(count: Int) {

    val brush = shimmerBrush()

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(66.dp)
                        .height(92.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
                )
                Spacer(Modifier.width(14.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(11.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}


// ======================================================
// ERREUR / VIDE
// ======================================================

@Composable
fun ErrorView(
    message: String,
    onRetry: (() -> Unit)? = null
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 32.dp)
            .entrance(0),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "😕", fontSize = 44.sp)

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Erreur lors du chargement",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = message,
            fontSize = 13.sp,
            color = GrayText,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Réessayer", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun EmptyView(text: String) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp)
            .entrance(0),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "🔍", fontSize = 44.sp)

        Spacer(Modifier.height(10.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            color = GrayText,
            textAlign = TextAlign.Center
        )
    }
}


// ======================================================
// LIVRE POSÉ SUR UNE ÉTAGÈRE EN BOIS
// ======================================================

@Composable
fun ModernBookCard(
    book: Book,
    rank: Int,
    onClick: () -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.width(148.dp)
    ) {

        // Couverture posée sur la planche
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .pressScale(onClick = onClick)
        ) {

            BookCover(
                coverId = book.coverId,
                title = book.title,
                modifier = Modifier
                    .width(132.dp)
                    .height(190.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 14.dp, bottomEnd = 2.dp, bottomStart = 2.dp)
                    ),
                corner = 8.dp,
                emojiSize = 36
            )

            // Badge de rang
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.55f)
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Planche en bois
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .shadow(6.dp, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(ShelfBrush)
        )

        Spacer(Modifier.height(10.dp))

        Column(modifier = Modifier.padding(horizontal = 8.dp)) {

            Text(
                text = book.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp,
                modifier = Modifier.clickable { onClick() }
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = book.author,
                fontSize = 11.sp,
                color = PrimaryPurple,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onAuthorClick() }
            )

            Text(
                text = book.year,
                fontSize = 11.sp,
                color = LightGrayText
            )
        }
    }
}


// ======================================================
// LIVRE RÉCENT
// ======================================================

@Composable
fun ModernRecentBook(
    book: Book,
    onClick: () -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier,
    isNew: Boolean = false
) {

    val accent = CoverGradients[(book.title.hashCode() and Int.MAX_VALUE) % CoverGradients.size][0]

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(0.97f, onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Bande de couleur sur le côté
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(118.dp)
                    .background(accent)
            )

            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                BookCover(
                    coverId = book.coverId,
                    title = book.title,
                    modifier = Modifier
                        .width(66.dp)
                        .height(92.dp),
                    corner = 10.dp,
                    emojiSize = 24
                )

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.width(150.dp)) {

                    if (isNew) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .graphicsLayer { alpha = pulse }
                                    .clip(CircleShape)
                                    .background(Coral)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "NOUVEAU",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                color = Coral
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                    }

                    Text(
                        text = book.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = book.author,
                        fontSize = 12.sp,
                        color = PrimaryPurple,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onAuthorClick() }
                    )

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightPurple
                    ) {
                        Text(
                            text = book.year,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryPurple,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Surface(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(34.dp),
                shape = CircleShape,
                color = AppBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Ouvrir",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}


// ======================================================
// BOUTON RETOUR
// ======================================================

@Composable
private fun BackButton(
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .pressScale(0.88f, onBack),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "‹",
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}


// ======================================================
// EXPLOSION DE CŒURS (favori)
// ======================================================

@Composable
private fun HeartBurst(trigger: Int) {

    val hearts = remember { List(7) { Animatable(1f) } }

    LaunchedEffect(trigger) {
        if (trigger > 0) {
            hearts.forEachIndexed { i, anim ->
                launch {
                    anim.snapTo(0f)
                    delay(i * 60L)
                    anim.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
                }
            }
        }
    }

    Box {
        hearts.forEachIndexed { i, anim ->
            Text(
                text = if (i % 2 == 0) "❤️" else "💜",
                fontSize = 22.sp,
                modifier = Modifier.graphicsLayer {
                    val p = anim.value
                    translationY = -p * 260f
                    translationX = (i - 3) * p * 46f
                    alpha = if (p >= 1f) 0f else 1f - p
                    val s = 0.6f + p * 0.8f
                    scaleX = s
                    scaleY = s
                }
            )
        }
    }
}


// ======================================================
// PAGE DÉTAIL DU LIVRE
// ======================================================

@Composable
fun BookDetailScreen(
    book: Book,
    onBack: () -> Unit,
    onAuthorClick: () -> Unit
) {

    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val dao = database.favoriteDao()
    val scope = rememberCoroutineScope()

    var isFavorite by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }

    val scrollState = rememberScrollState()
    val heartScale = remember { Animatable(1f) }

    LaunchedEffect(book.coverId) {
        if (book.coverId != null) {
            isFavorite = dao.isFavorite(book.coverId)
        }
    }

    val buttonColor by animateColorAsState(
        targetValue = if (isFavorite) LightPurple else PrimaryPurple,
        animationSpec = tween(350),
        label = "favButton"
    )
    val buttonContent by animateColorAsState(
        targetValue = if (isFavorite) PrimaryPurple else Color.White,
        animationSpec = tween(350),
        label = "favContent"
    )
    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) FavoriteRed else Color.White,
        animationSpec = tween(350),
        label = "favHeart"
    )

    // Halo lumineux qui respire derrière la couverture
    val glowTransition = rememberInfiniteTransition(label = "glow")
    val glow by glowTransition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
    ) {

        // ================= HEADER + COUVERTURE =================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .headerBackground(40.dp)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                BackButton(onBack = onBack)
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .entrance(0, 40.dp)
                    .graphicsLayer {
                        val s = scrollState.value.toFloat()
                        translationY = s * 0.45f
                        alpha = (1f - s / 800f).coerceIn(0f, 1f)
                        val scale = (1f - s / 2500f).coerceIn(0.8f, 1f)
                        scaleX = scale
                        scaleY = scale
                    }
                    .drawBehind {
                        drawCircle(
                            color = Color.White.copy(alpha = glow),
                            radius = size.width * 0.8f
                        )
                    }
            ) {
                BookCover(
                    coverId = book.coverId,
                    title = book.title,
                    modifier = Modifier
                        .size(width = 200.dp, height = 290.dp)
                        .shadow(22.dp, RoundedCornerShape(20.dp)),
                    corner = 20.dp,
                    sizeCode = "L",
                    emojiSize = 60
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {

            // ================= TITRE =================

            Text(
                text = book.title,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText,
                lineHeight = 33.sp,
                modifier = Modifier.entrance(1)
            )

            Spacer(Modifier.height(16.dp))

            // ================= INFOS =================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InfoCard(
                    emoji = "✍️",
                    label = "Auteur",
                    value = book.author,
                    valueColor = PrimaryPurple,
                    modifier = Modifier
                        .weight(1.3f)
                        .pressScale(0.96f, onAuthorClick)
                )

                InfoCard(
                    emoji = "📅",
                    label = "Publication",
                    value = book.year,
                    valueColor = DarkText,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(22.dp))

            // ================= FAVORI =================

            if (book.coverId != null) {

                Box(contentAlignment = Alignment.Center) {

                    Button(
                        onClick = {
                            scope.launch {
                                val entity = FavoriteEntity(
                                    coverId = book.coverId,
                                    title = book.title,
                                    author = book.author,
                                    year = book.year
                                )

                                if (isFavorite) {
                                    dao.deleteFavorite(entity)
                                    isFavorite = false
                                } else {
                                    dao.insertFavorite(entity)
                                    isFavorite = true
                                    burst++
                                    heartScale.animateTo(1.6f, tween(120))
                                    heartScale.animateTo(
                                        1f,
                                        spring(dampingRatio = Spring.DampingRatioHighBouncy)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .entrance(3),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = buttonContent
                        )
                    ) {

                        Icon(
                            imageVector = if (isFavorite)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = heartColor,
                            modifier = Modifier.graphicsLayer {
                                scaleX = heartScale.value
                                scaleY = heartScale.value
                            }
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    HeartBurst(trigger = burst)
                }

                Spacer(Modifier.height(24.dp))
            }

            // ================= DESCRIPTION =================

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(4),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {

                    Text(
                        text = "📖 Informations sur le livre",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Ce livre est actuellement disponible dans votre bibliothèque Open Library.",
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = GrayText
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun InfoCard(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightGrayText,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// ======================================================
// PAGE TOUS LES LIVRES D'UN AUTEUR
// ======================================================

@Composable
fun AuthorBooksScreen(
    author: String,
    onBack: () -> Unit,
    onBookClick: (Book) -> Unit
) {

    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(author, reloadKey) {
        isLoading = true
        errorMessage = null
        try {
            val response = RetrofitInstance.api.searchBooksByAuthor(
                author = author,
                limit = 50
            )

            books = response.docs
                ?.mapNotNull { doc ->
                    val title = doc.title ?: return@mapNotNull null
                    Book(
                        title = title,
                        author = doc.author_name?.joinToString(", ") ?: author,
                        year = doc.first_publish_year?.toString() ?: "Année inconnue",
                        coverId = doc.cover_i
                    )
                }
                ?.distinctBy { it.title }
                ?: emptyList()

        } catch (e: Exception) {
            errorMessage = e.message ?: "Erreur inconnue"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        // ================= HEADER =================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .headerBackground(32.dp)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BackButton(onBack = onBack)

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .entrance(0, 12.dp)
            ) {

                Text(
                    text = "✍️ AUTEUR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = author,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ================= CONTENU =================

        when {
            isLoading -> {
                Spacer(Modifier.height(22.dp))
                SkeletonList(count = 5)
            }

            errorMessage != null -> ErrorView(
                message = errorMessage ?: "Erreur inconnue",
                onRetry = { reloadKey++ }
            )

            books.isEmpty() -> EmptyView("Aucun livre trouvé pour cet auteur.")

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Text(
                        text = "📚 ${books.size} livre(s) trouvé(s)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = GrayText
                    )
                }

                itemsIndexed(books) { index, book ->
                    ModernRecentBook(
                        book = book,
                        onClick = { onBookClick(book) },
                        onAuthorClick = {},
                        modifier = Modifier.entrance(index, 28.dp)
                    )
                }
            }
        }
    }
}