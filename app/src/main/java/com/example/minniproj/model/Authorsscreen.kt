package com.example.minniproj

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.minniproj.model.AuthorSearchResponse
import com.example.minniproj.model.Book
import com.example.minniproj.model.ModernRecentBook
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.Normalizer


// ======================================================
// API : AUTEURS (Open Library)
// ======================================================

data class AuthorListResponse(
    val docs: List<AuthorInfo>?
)

data class AuthorInfo(
    val key: String?,
    val name: String?,
    val birth_date: String?,
    val death_date: String?,
    val top_work: String?,
    val work_count: Int?
)

interface AuthorsApi {

    // Recherche d'auteurs par nom
    @GET("search/authors.json")
    suspend fun searchAuthors(
        @Query("q") query: String,
        @Query("limit") limit: Int = 30
    ): AuthorListResponse

    // Livres d'UN auteur précis, grâce à son identifiant (ex : OL23919A)
    @GET("search.json")
    suspend fun worksByAuthorKey(
        @Query("author_key") authorKey: String,
        @Query("limit") limit: Int = 100
    ): AuthorSearchResponse
}

private object AuthorsRetrofit {

    val api: AuthorsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthorsApi::class.java)
    }
}


// ======================================================
// COULEURS
// ======================================================

private val AuBackground = Color(0xFFFBF7F1)
private val AuPurple = Color(0xFF5B3FC4)
private val AuDeepPurple = Color(0xFF2E1C80)
private val AuSoftPurple = Color(0xFF9378F5)
private val AuLightPurple = Color(0xFFEDE8FF)
private val AuCoral = Color(0xFFFF6B6B)
private val AuAmber = Color(0xFFFFB347)
private val AuDarkText = Color(0xFF211F36)
private val AuGrayText = Color(0xFF74728A)
private val AuLightGray = Color(0xFF9996A8)

private val AuHeaderBrush = Brush.linearGradient(
    colors = listOf(AuDeepPurple, AuPurple, AuSoftPurple)
)

private val AuGradients = listOf(
    listOf(Color(0xFF7F5AF0), Color(0xFF5B3FC4)),
    listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)),
    listOf(Color(0xFF00B894), Color(0xFF0984E3)),
    listOf(Color(0xFFF6A04D), Color(0xFFF6D365)),
    listOf(Color(0xFFE84393), Color(0xFF6C5CE7)),
    listOf(Color(0xFF2D3A8C), Color(0xFF4FACFE))
)

// Noms utilisés pour les puces de suggestion ET pour la liste d'auteurs populaires
private val SuggestedAuthors = listOf(
    "Victor Hugo",
    "Agatha Christie",
    "Albert Camus",
    "Jules Verne",
    "J. K. Rowling",
    "Molière",
    "Stephen King",
    "Tolkien"
)


// ======================================================
// HELPERS DE RECHERCHE
// ======================================================

/** Minuscules, sans accents, sans espaces autour : "Molière " -> "moliere". */
private fun String.normalized(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()
        .trim()

/** Vrai si chaque mot tapé apparaît dans le nom de l'auteur (ordre libre). */
private fun matchesQuery(name: String?, query: String): Boolean {
    if (name.isNullOrBlank()) return false
    val n = name.normalized()
    return query.normalized()
        .split(" ")
        .filter { it.isNotBlank() }
        .all { n.contains(it) }
}


// ======================================================
// HELPERS D'ANIMATION
// ======================================================

@Composable
private fun Modifier.auPressScale(
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
        label = "auPress"
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

@Composable
private fun Modifier.auEntrance(
    index: Int,
    offsetY: Dp = 28.dp
): Modifier {
    var played by rememberSaveable { mutableStateOf(false) }
    val progress = remember { Animatable(if (played) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!played) {
            delay((index * 60L).coerceAtMost(400L))
            progress.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
            played = true
        }
    }

    val offsetPx = with(LocalDensity.current) { offsetY.toPx() }

    return this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * offsetPx
    }
}

@Composable
private fun Modifier.auHeaderBackground(corner: Dp = 32.dp): Modifier {
    val transition = rememberInfiniteTransition(label = "auHeader")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auDrift"
    )

    return this
        .clip(RoundedCornerShape(bottomStart = corner, bottomEnd = corner))
        .background(AuHeaderBrush)
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
                color = AuCoral.copy(alpha = 0.22f),
                radius = size.width * 0.16f,
                center = Offset(
                    x = size.width * (0.55f + 0.08f * drift),
                    y = size.height * (0.9f - 0.2f * drift)
                )
            )
            drawCircle(
                color = AuAmber.copy(alpha = 0.20f),
                radius = size.width * 0.10f,
                center = Offset(
                    x = size.width * (0.08f + 0.07f * drift),
                    y = size.height * (0.30f + 0.2f * drift)
                )
            )
        }
}

@Composable
private fun auShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "auShimmer")
    val x by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing)
        ),
        label = "auShimmerX"
    )
    return Brush.linearGradient(
        colors = listOf(Color(0xFFEBE4D8), Color(0xFFFAF6EF), Color(0xFFEBE4D8)),
        start = Offset(x - 400f, 0f),
        end = Offset(x, 300f)
    )
}

@Composable
private fun AuBackButton(onBack: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .auPressScale(0.88f, onBack),
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
// ÉTAT DE LA RECHERCHE
// ======================================================

private sealed class AuthorsState {
    object Idle : AuthorsState()
    object Loading : AuthorsState()
    data class Success(val authors: List<AuthorInfo>) : AuthorsState()
    data class Error(val message: String) : AuthorsState()
}


// ======================================================
// ÉCRAN : RECHERCHE D'AUTEURS
// ======================================================

/**
 * @param onBack        null = onglet racine (pas de bouton retour affiché)
 * @param onAuthorClick (identifiant Open Library, nom de l'auteur)
 */
@Composable
fun AuthorsScreen(
    onAuthorClick: (key: String, name: String) -> Unit,
    onBack: (() -> Unit)? = null
) {

    var query by rememberSaveable { mutableStateOf("") }
    var state by remember { mutableStateOf<AuthorsState>(AuthorsState.Idle) }
    var retryKey by remember { mutableStateOf(0) }

    var featured by remember { mutableStateOf<List<AuthorInfo>>(emptyList()) }
    var featuredLoading by remember { mutableStateOf(true) }

    // Chargé une seule fois : le meilleur résultat pour chaque nom suggéré
    LaunchedEffect(Unit) {
        featured = try {
            coroutineScope {
                SuggestedAuthors.map { name ->
                    async {
                        runCatching {
                            AuthorsRetrofit.api.searchAuthors(name, 10).docs
                                ?.filter { matchesQuery(it.name, name) && !it.key.isNullOrBlank() }
                                ?.maxByOrNull { it.work_count ?: 0 }
                        }.getOrNull()
                    }
                }.awaitAll().filterNotNull().distinctBy { it.key }
            }
        } catch (e: Exception) {
            emptyList()
        }
        featuredLoading = false
    }

    // Recherche avec "debounce" : on attend 400 ms après la dernière frappe
    LaunchedEffect(query, retryKey) {

        val q = query.trim()

        if (q.length < 2) {
            state = AuthorsState.Idle
            return@LaunchedEffect
        }

        state = AuthorsState.Loading
        delay(400)

        state = try {
            val response = AuthorsRetrofit.api.searchAuthors(q)

            val authors = response.docs
                ?.filter {
                    !it.key.isNullOrBlank() &&
                            matchesQuery(it.name, q) &&          // seulement les noms qui contiennent la recherche
                            (it.work_count ?: 0) > 0             // on cache les fiches vides
                }
                ?.distinctBy { it.key }
                ?.sortedByDescending { it.work_count ?: 0 }      // les plus connus d'abord
                ?: emptyList()

            AuthorsState.Success(authors)

        } catch (e: Exception) {
            AuthorsState.Error(e.message ?: "Erreur inconnue")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuBackground)
    ) {

        // ================= HEADER =================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .auHeaderBackground(36.dp)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 26.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .auEntrance(0, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (onBack != null) {
                    AuBackButton(onBack = onBack)
                    Spacer(Modifier.width(14.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auteurs ✍️",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Trouvez votre prochain auteur préféré",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(modifier = Modifier.auEntrance(1, 16.dp)) {
                AuthorSearchBar(
                    value = query,
                    onValueChange = { query = it }
                )
            }
        }

        // ================= CONTENU =================

        when (val current = state) {

            is AuthorsState.Idle -> FeaturedContent(
                authors = featured,
                loading = featuredLoading,
                onSuggestionClick = { query = it },
                onAuthorClick = onAuthorClick
            )

            is AuthorsState.Loading -> {
                Spacer(Modifier.height(22.dp))
                AuthorsSkeleton()
            }

            is AuthorsState.Error -> AuthorsError(
                message = current.message,
                onRetry = { retryKey++ }
            )

            is AuthorsState.Success -> {

                if (current.authors.isEmpty()) {
                    AuthorsEmpty(
                        title = "Aucun auteur pour « ${query.trim()} »",
                        subtitle = "Vérifiez l'orthographe ou essayez un autre nom."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        item {
                            Text(
                                text = "✍️ ${current.authors.size} auteur(s) trouvé(s)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AuGrayText
                            )
                        }

                        itemsIndexed(
                            items = current.authors,
                            key = { _, author -> author.key ?: "" }
                        ) { index, author ->
                            AuthorCard(
                                author = author,
                                onClick = {
                                    onAuthorClick(
                                        author.key ?: "",
                                        author.name ?: ""
                                    )
                                },
                                modifier = Modifier.auEntrance(index, 28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ======================================================
// ÉCRAN : LIVRES D'UN AUTEUR (par identifiant)
// ======================================================

@Composable
fun AuthorWorksScreen(
    authorKey: String,
    authorName: String,
    onBack: () -> Unit,
    onBookClick: (Book) -> Unit
) {

    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(authorKey, reloadKey) {
        isLoading = true
        errorMessage = null
        try {
            val response = AuthorsRetrofit.api.worksByAuthorKey(authorKey)

            books = response.docs
                ?.mapNotNull { doc ->
                    val title = doc.title ?: return@mapNotNull null
                    Book(
                        title = title,
                        author = authorName,
                        year = doc.first_publish_year?.toString() ?: "Année inconnue",
                        coverId = doc.cover_i
                    )
                }
                ?.distinctBy { it.title.trim().lowercase() }
                ?.sortedBy { it.coverId == null }   // les livres avec couverture d'abord
                ?: emptyList()

        } catch (e: Exception) {
            errorMessage = e.message ?: "Erreur inconnue"
        } finally {
            isLoading = false
        }
    }

    val gradient = AuGradients[(authorName.hashCode() and Int.MAX_VALUE) % AuGradients.size]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuBackground)
    ) {

        // ================= HEADER =================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .auHeaderBackground(32.dp)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AuBackButton(onBack = onBack)

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .auEntrance(0, 12.dp)
            ) {

                Text(
                    text = "✍️ AUTEUR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = authorName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(12.dp))

            AuthorAvatar(
                name = authorName,
                olid = authorKey,
                gradient = gradient,
                size = 54.dp
            )
        }

        // ================= CONTENU =================

        when {
            isLoading -> {
                Spacer(Modifier.height(22.dp))
                AuthorsSkeleton()
            }

            errorMessage != null -> AuthorsError(
                message = errorMessage ?: "Erreur inconnue",
                onRetry = { reloadKey++ }
            )

            books.isEmpty() -> AuthorsEmpty(
                title = "Aucun livre trouvé",
                subtitle = "Cet auteur n'a pas encore de livres dans Open Library."
            )

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
                        color = AuGrayText
                    )
                }

                itemsIndexed(books) { index, book ->
                    ModernRecentBook(
                        book = book,
                        onClick = { onBookClick(book) },
                        onAuthorClick = {},
                        modifier = Modifier.auEntrance(index, 28.dp)
                    )
                }
            }
        }
    }
}


// ======================================================
// BARRE DE RECHERCHE
// ======================================================

@Composable
private fun AuthorSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {

    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()

    val elevation by animateDpAsState(
        targetValue = if (focused) 14.dp else 4.dp,
        label = "auSearchElevation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (focused) AuAmber else Color.Transparent,
        label = "auSearchBorder"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (focused) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "auSearchIcon"
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
                tint = AuPurple,
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
                textStyle = TextStyle(fontSize = 15.sp, color = AuDarkText),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = "Rechercher un auteur...",
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
                    color = AuLightPurple
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Effacer",
                            tint = AuPurple,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}


// ======================================================
// ÉTAT INITIAL : suggestions + auteurs populaires
// ======================================================

@Composable
private fun FeaturedContent(
    authors: List<AuthorInfo>,
    loading: Boolean,
    onSuggestionClick: (String) -> Unit,
    onAuthorClick: (key: String, name: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 22.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "💡 Suggestions",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuDarkText,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .auEntrance(0, 12.dp)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(SuggestedAuthors) { index, name ->
                    Surface(
                        modifier = Modifier
                            .auEntrance(index + 1, 16.dp)
                            .auPressScale(0.92f) { onSuggestionClick(name) },
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Text(
                            text = name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AuPurple,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        item {

        }

        when {
            loading -> item { AuthorsSkeleton() }

            authors.isEmpty() -> item {
                Text(
                    text = "Impossible de charger les auteurs. Utilisez la recherche ci-dessus.",
                    fontSize = 13.sp,
                    color = AuGrayText,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            else -> itemsIndexed(
                items = authors,
                key = { _, a -> a.key ?: "" }
            ) { index, author ->
                AuthorCard(
                    author = author,
                    onClick = { onAuthorClick(author.key ?: "", author.name ?: "") },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .auEntrance(index, 28.dp)
                )
            }
        }
    }
}


// ======================================================
// CARTE AUTEUR
// ======================================================

@Composable
private fun AuthorCard(
    author: AuthorInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val name = author.name ?: "Auteur inconnu"
    val gradient = AuGradients[(name.hashCode() and Int.MAX_VALUE) % AuGradients.size]

    val lifeSpan = listOfNotNull(
        author.birth_date?.takeIf { it.isNotBlank() },
        author.death_date?.takeIf { it.isNotBlank() }
    ).joinToString(" – ")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .auPressScale(0.97f, onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AuthorAvatar(
                name = name,
                olid = author.key,
                gradient = gradient
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AuDarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (lifeSpan.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = lifeSpan,
                        fontSize = 12.sp,
                        color = AuLightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!author.top_work.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "📖 ${author.top_work}",
                        fontSize = 12.sp,
                        color = AuPurple,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (author.work_count != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AuLightPurple
                    ) {
                        Text(
                            text = "${author.work_count} œuvre(s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuPurple,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = AuBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Voir les livres",
                        tint = AuPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/** Photo de l'auteur si elle existe, sinon initiales sur fond coloré. */
@Composable
private fun AuthorAvatar(
    name: String,
    olid: String?,
    gradient: List<Color>,
    size: Dp = 68.dp
) {

    val context = LocalContext.current

    val initials = name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = initials,
            fontSize = (size.value * 0.32f).sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        if (!olid.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("https://covers.openlibrary.org/a/olid/$olid-M.jpg?default=false")
                    .crossfade(450)
                    .build(),
                contentDescription = "Photo de $name",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


// ======================================================
// CHARGEMENT / ERREUR / VIDE
// ======================================================

@Composable
private fun AuthorsSkeleton() {

    val brush = auShimmerBrush()

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Spacer(Modifier.width(14.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(11.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthorsError(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp)
            .auEntrance(0),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "😕", fontSize = 44.sp)

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Erreur lors du chargement",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = AuDarkText
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = message,
            fontSize = 13.sp,
            color = AuGrayText,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AuPurple)
        ) {
            Text("Réessayer", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AuthorsEmpty(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .auEntrance(0),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "🔍", fontSize = 44.sp)

        Spacer(Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AuDarkText,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = AuGrayText,
            textAlign = TextAlign.Center
        )
    }
}