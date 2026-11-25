package com.example.minniproj

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.minniproj.model.FavoriteEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// ======================================================
// COULEURS (même palette que l'accueil)
// ======================================================

private val FavBackground = Color(0xFFFBF7F1)
private val FavPurple = Color(0xFF5B3FC4)
private val FavDeepPurple = Color(0xFF2E1C80)
private val FavSoftPurple = Color(0xFF9378F5)
private val FavLightPurple = Color(0xFFEDE8FF)
private val FavCoral = Color(0xFFFF6B6B)
private val FavAmber = Color(0xFFFFB347)
private val FavDarkText = Color(0xFF211F36)
private val FavGrayText = Color(0xFF74728A)

private val FavHeaderBrush = Brush.linearGradient(
    colors = listOf(FavDeepPurple, FavPurple, FavSoftPurple)
)

private val FavCoverGradients = listOf(
    listOf(Color(0xFF7F5AF0), Color(0xFF5B3FC4)),
    listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)),
    listOf(Color(0xFF00B894), Color(0xFF0984E3)),
    listOf(Color(0xFFF6A04D), Color(0xFFF6D365)),
    listOf(Color(0xFFE84393), Color(0xFF6C5CE7)),
    listOf(Color(0xFF2D3A8C), Color(0xFF4FACFE))
)


// ======================================================
// HELPERS D'ANIMATION
// ======================================================

@Composable
private fun Modifier.favPressScale(
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
        label = "favPress"
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
private fun Modifier.favEntrance(
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

@Composable
private fun Modifier.favHeaderBackground(corner: Dp = 32.dp): Modifier {
    val transition = rememberInfiniteTransition(label = "favHeader")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "favDrift"
    )

    return this
        .clip(RoundedCornerShape(bottomStart = corner, bottomEnd = corner))
        .background(FavHeaderBrush)
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
                color = FavCoral.copy(alpha = 0.22f),
                radius = size.width * 0.16f,
                center = Offset(
                    x = size.width * (0.55f + 0.08f * drift),
                    y = size.height * (0.9f - 0.2f * drift)
                )
            )
            drawCircle(
                color = FavAmber.copy(alpha = 0.20f),
                radius = size.width * 0.10f,
                center = Offset(
                    x = size.width * (0.08f + 0.07f * drift),
                    y = size.height * (0.30f + 0.2f * drift)
                )
            )
        }
}


// ======================================================
// FAVORITES SCREEN
// ======================================================

@Composable
fun FavoritesScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val dao = database.favoriteDao()
    val scope = rememberCoroutineScope()

    var favorites by remember { mutableStateOf<List<FavoriteEntity>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        favorites = dao.getAllFavorites()
        loaded = true
    }

    val animatedCount by animateIntAsState(
        targetValue = favorites.size,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "favCount"
    )

    // Cœur qui bat dans l'en-tête
    val heartTransition = rememberInfiniteTransition(label = "heart")
    val beat by heartTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beat"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FavBackground)
    ) {

        // ================= HEADER =================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .favHeaderBackground(36.dp)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Bouton retour
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .favPressScale(0.88f, onBack),
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

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .favEntrance(0, 12.dp)
            ) {
                Text(
                    text = "Mes favoris",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$animatedCount livre(s) enregistré(s)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "❤️",
                        fontSize = 26.sp,
                        modifier = Modifier.graphicsLayer {
                            scaleX = beat
                            scaleY = beat
                        }
                    )
                }
            }
        }

        // ================= CONTENU =================

        when {
            !loaded -> Unit

            favorites.isEmpty() -> EmptyFavorites()

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 22.dp, bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                itemsIndexed(
                    items = favorites,
                    key = { _, favorite -> favorite.coverId }
                ) { index, favorite ->

                    FavoriteItem(
                        favorite = favorite,
                        modifier = Modifier.favEntrance(index, 32.dp),
                        onDelete = {
                            scope.launch {
                                dao.deleteFavorite(favorite)
                                favorites = dao.getAllFavorites()
                            }
                        }
                    )
                }
            }
        }
    }
}


// ======================================================
// AUCUN FAVORI
// ======================================================

@Composable
private fun EmptyFavorites() {

    val transition = rememberInfiniteTransition(label = "emptyBob")
    val bob by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyBobY"
    )
    val tilt by transition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyTilt"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp, start = 32.dp, end = 32.dp)
            .favEntrance(0, 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(FavLightPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📚",
                fontSize = 54.sp,
                modifier = Modifier.graphicsLayer {
                    translationY = bob
                    rotationZ = tilt
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Aucun favori",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = FavDarkText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Ajoutez des livres à vos favoris\npour les retrouver ici.",
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = FavGrayText,
            textAlign = TextAlign.Center
        )
    }
}


// ======================================================
// ITEM FAVORI
// ======================================================

@Composable
fun FavoriteItem(
    favorite: FavoriteEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(true) }

    val gradient = FavCoverGradients[
        (favorite.title.hashCode() and Int.MAX_VALUE) % FavCoverGradients.size
    ]

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        exit = slideOutHorizontally(tween(300)) { it } +
                shrinkVertically(tween(300)) +
                fadeOut(tween(250))
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Bande de couleur
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(124.dp)
                        .background(gradient[0])
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Couverture avec fond coloré en attendant l'image
                    Box(
                        modifier = Modifier
                            .size(width = 70.dp, height = 100.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(gradient)),
                        contentAlignment = Alignment.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .width(5.dp)
                                .background(Color.Black.copy(alpha = 0.18f))
                        )

                        Text(
                            text = favorite.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("https://covers.openlibrary.org/b/id/${favorite.coverId}-M.jpg")
                                .crossfade(450)
                                .build(),
                            contentDescription = "Couverture de ${favorite.title}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = favorite.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FavDarkText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = favorite.author,
                            fontSize = 13.sp,
                            color = FavPurple,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FavLightPurple
                        ) {
                            Text(
                                text = favorite.year,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = FavPurple,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Bouton supprimer (la ligne glisse puis disparaît)
                Surface(
                    modifier = Modifier
                        .padding(end = 14.dp)
                        .size(40.dp)
                        .favPressScale(0.85f) {
                            scope.launch {
                                visible = false
                                delay(320)
                                onDelete()
                            }
                        },
                    shape = CircleShape,
                    color = FavCoral.copy(alpha = 0.14f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.poubelle),
                            contentDescription = "Supprimer",
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            }
        }
    }
}