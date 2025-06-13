package cc.worldmandia

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime

// Domain Models
data class Feature(val title: String, val icon: ImageVector, val description: String)
data class AppState(val isDark: Boolean = false, val currentTime: String = "", val isLoading: Boolean = false)
data class Stats(val performance: String, val support: String, val users: String, val rating: String)

// Repositories
interface TimeRepository {
    suspend fun getCurrentTime(): String
}

class TimeRepositoryImpl : TimeRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun getCurrentTime(): String {
        return "time like 22:30"
    }
}

interface FeatureRepository {
    fun getFeatures(): List<Feature>
}

class FeatureRepositoryImpl : FeatureRepository {
    override fun getFeatures() = listOf(
        Feature("Performance", Icons.Default.Speed, "Lightning fast with Wasm"),
        Feature("Design", Icons.Default.Palette, "Material Design 3"),
        Feature("Responsive", Icons.Default.Devices, "All screen sizes"),
        Feature("Type Safe", Icons.Default.Security, "Kotlin everywhere")
    )
}

interface StatsRepository {
    fun getStats(): Stats
}

class StatsRepositoryImpl : StatsRepository {
    override fun getStats() = Stats("99%", "24/7", "1M+", "5 stars")
}

// Services
interface ThemeService {
    val isDarkMode: MutableState<Boolean>
    val isTransitioning: MutableState<Boolean>
    val rippleProgress: MutableState<Float>
    val rippleCenter: MutableState<Pair<Float, Float>>
    fun toggleTheme()
    fun startRipple(centerX: Float, centerY: Float)
}

class ThemeServiceImpl : ThemeService {
    override val isDarkMode = mutableStateOf(false)
    override val isTransitioning = mutableStateOf(false)
    override val rippleProgress = mutableStateOf(0f)
    override val rippleCenter = mutableStateOf(0f to 0f)

    override fun toggleTheme() {
        isTransitioning.value = true
        isDarkMode.value = !isDarkMode.value
    }

    override fun startRipple(centerX: Float, centerY: Float) {
        rippleCenter.value = centerX to centerY
        rippleProgress.value = 0f
    }
}

interface AppService {
    val state: MutableState<AppState>
    suspend fun updateTime()
    fun setLoading(loading: Boolean)
}

class AppServiceImpl(
    private val timeRepository: TimeRepository,
    private val themeService: ThemeService
) : AppService {
    override val state = mutableStateOf(AppState())

    override suspend fun updateTime() {
        state.value = state.value.copy(
            currentTime = timeRepository.getCurrentTime(),
        )
    }

    override fun setLoading(loading: Boolean) {
        state.value = state.value.copy(isLoading = loading)
    }
}

// ViewModels
class MainViewModel(
    private val appService: AppService,
    private val featureRepository: FeatureRepository,
    private val statsRepository: StatsRepository
) {
    val appState by appService.state
    val features = featureRepository.getFeatures()
    val stats = statsRepository.getStats()

    suspend fun startTimeUpdates() {
        while (true) {
            appService.updateTime()
            delay(60000)
        }
    }

    fun setLoading(loading: Boolean) = appService.setLoading(loading)
}

// DI Module
val appModule = module {
    singleOf(::TimeRepositoryImpl) bind TimeRepository::class
    singleOf(::FeatureRepositoryImpl) bind FeatureRepository::class
    singleOf(::StatsRepositoryImpl) bind StatsRepository::class
    singleOf(::ThemeServiceImpl) bind ThemeService::class
    singleOf(::AppServiceImpl) bind AppService::class
    singleOf(::MainViewModel)
}

// Enhanced Theme Animation
@Composable
fun rememberSmoothAnimatedTheme(isDark: Boolean): ColorScheme {
    val colors = if (isDark) darkColorScheme() else lightColorScheme()
    val animationSpec = spring<Color>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    return colors.copy(
        primary = animateColorAsState(colors.primary, animationSpec, label = "primary").value,
        onPrimary = animateColorAsState(colors.onPrimary, animationSpec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(
            colors.primaryContainer,
            animationSpec,
            label = "primaryContainer"
        ).value,
        onPrimaryContainer = animateColorAsState(
            colors.onPrimaryContainer,
            animationSpec,
            label = "onPrimaryContainer"
        ).value,
        secondary = animateColorAsState(colors.secondary, animationSpec, label = "secondary").value,
        onSecondary = animateColorAsState(colors.onSecondary, animationSpec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(
            colors.secondaryContainer,
            animationSpec,
            label = "secondaryContainer"
        ).value,
        onSecondaryContainer = animateColorAsState(
            colors.onSecondaryContainer,
            animationSpec,
            label = "onSecondaryContainer"
        ).value,
        tertiary = animateColorAsState(colors.tertiary, animationSpec, label = "tertiary").value,
        onTertiary = animateColorAsState(colors.onTertiary, animationSpec, label = "onTertiary").value,
        tertiaryContainer = animateColorAsState(
            colors.tertiaryContainer,
            animationSpec,
            label = "tertiaryContainer"
        ).value,
        onTertiaryContainer = animateColorAsState(
            colors.onTertiaryContainer,
            animationSpec,
            label = "onTertiaryContainer"
        ).value,
        error = animateColorAsState(colors.error, animationSpec, label = "error").value,
        onError = animateColorAsState(colors.onError, animationSpec, label = "onError").value,
        errorContainer = animateColorAsState(colors.errorContainer, animationSpec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(
            colors.onErrorContainer,
            animationSpec,
            label = "onErrorContainer"
        ).value,
        surface = animateColorAsState(colors.surface, animationSpec, label = "surface").value,
        onSurface = animateColorAsState(colors.onSurface, animationSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(colors.surfaceVariant, animationSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(
            colors.onSurfaceVariant,
            animationSpec,
            label = "onSurfaceVariant"
        ).value,
        outline = animateColorAsState(colors.outline, animationSpec, label = "outline").value,
        outlineVariant = animateColorAsState(colors.outlineVariant, animationSpec, label = "outlineVariant").value,
        scrim = animateColorAsState(colors.scrim, animationSpec, label = "scrim").value,
        inverseSurface = animateColorAsState(colors.inverseSurface, animationSpec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(
            colors.inverseOnSurface,
            animationSpec,
            label = "inverseOnSurface"
        ).value,
        inversePrimary = animateColorAsState(colors.inversePrimary, animationSpec, label = "inversePrimary").value,
        surfaceDim = animateColorAsState(colors.surfaceDim, animationSpec, label = "surfaceDim").value,
        surfaceBright = animateColorAsState(colors.surfaceBright, animationSpec, label = "surfaceBright").value,
        surfaceContainerLowest = animateColorAsState(
            colors.surfaceContainerLowest,
            animationSpec,
            label = "surfaceContainerLowest"
        ).value,
        surfaceContainerLow = animateColorAsState(
            colors.surfaceContainerLow,
            animationSpec,
            label = "surfaceContainerLow"
        ).value,
        surfaceContainer = animateColorAsState(
            colors.surfaceContainer,
            animationSpec,
            label = "surfaceContainer"
        ).value,
        surfaceContainerHigh = animateColorAsState(
            colors.surfaceContainerHigh,
            animationSpec,
            label = "surfaceContainerHigh"
        ).value,
        surfaceContainerHighest = animateColorAsState(
            colors.surfaceContainerHighest,
            animationSpec,
            label = "surfaceContainerHighest"
        ).value,
        background = animateColorAsState(colors.background, animationSpec, label = "background").value,
        onBackground = animateColorAsState(colors.onBackground, animationSpec, label = "onBackground").value
    )
}

// Composables
@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val viewModel: MainViewModel = koinInject()
        val themeService: ThemeService = koinInject()
        val animatedTheme = rememberSmoothAnimatedTheme(themeService.isDarkMode.value)

        // Ripple animation effect
        val rippleProgress by animateFloatAsState(
            targetValue = if (themeService.isTransitioning.value) 1f else 0f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            label = "ripple_progress"
        )

        // Update ripple progress in service
        LaunchedEffect(rippleProgress) {
            themeService.rippleProgress.value = rippleProgress
        }

        // Reset transition state after animation completes
        LaunchedEffect(themeService.isDarkMode.value) {
            if (themeService.isTransitioning.value) {
                delay(1000) // Match ripple animation duration
                themeService.isTransitioning.value = false
            }
        }

        // Time updates effect
        LaunchedEffect(Unit) {
            viewModel.startTimeUpdates()
        }

        MaterialTheme(colorScheme = animatedTheme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box {
                    MainContent(viewModel)

                    // Ripple effect overlay
                    RippleOverlay(
                        progress = rippleProgress,
                        center = themeService.rippleCenter.value,
                        isDark = themeService.isDarkMode.value
                    )

                    EnhancedThemeToggle(
                        isDark = themeService.isDarkMode.value,
                        isTransitioning = themeService.isTransitioning.value,
                        onToggle = { centerX, centerY ->
                            themeService.startRipple(centerX, centerY)
                            themeService.toggleTheme()
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )

                    if (viewModel.appState.isLoading) {
                        LoadingOverlay(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(viewModel.appState.currentTime)
        HeroSection(onStartClick = { viewModel.setLoading(true) })
        FeatureGrid(viewModel.features)
        StatsRow(viewModel.stats)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(currentTime: String) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Code, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Kotlin WasmJs", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(16.dp))
                Text(currentTime, style = MaterialTheme.typography.bodyMedium)
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null) }
            IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) }
        }
    )
}

@Composable
fun HeroSection(onStartClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "hero_animation")
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "rocket_scale"
            )

            Icon(
                Icons.Default.Rocket,
                null,
                Modifier.size(64.dp).scale(animatedScale),
                MaterialTheme.colorScheme.primary
            )
            Text("Modern Web with Kotlin", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Fast • Type-safe • Beautiful", style = MaterialTheme.typography.bodyLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onStartClick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Start")
                }
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Info, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Learn")
                }
            }
        }
    }
}

@Composable
fun FeatureGrid(features: List<Feature>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(240.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(300.dp)
    ) {
        items(features) { feature ->
            FeatureCard(feature)
        }
    }
}

@Composable
fun FeatureCard(feature: Feature) {
    var hovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (hovered) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )
    val elevation by animateDpAsState(
        targetValue = if (hovered) 8.dp else 2.dp,
        animationSpec = tween(200),
        label = "card_elevation"
    )

    Card(
        modifier = Modifier.scale(scale),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(feature.icon, null, Modifier.size(40.dp), MaterialTheme.colorScheme.primary)
            Text(feature.title, fontWeight = FontWeight.SemiBold)
            Text(
                feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatsRow(stats: Stats) {
    val statsList = listOf(
        stats.performance to "Speed",
        stats.support to "Support",
        stats.users to "Users",
        stats.rating to "Rating"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        statsList.forEach { (value, label) ->
            StatItem(value, label)
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "stats_animation")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stat_alpha"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
        )
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

// Ripple Effect Overlay
@Composable
fun RippleOverlay(
    progress: Float,
    center: Pair<Float, Float>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    if (progress > 0f) {
        Canvas(
            modifier = modifier.fillMaxSize()
        ) {
            val (centerX, centerY) = center
            val maxRadius = sqrt(
                (size.width - centerX) * (size.width - centerX) +
                        (size.height - centerY) * (size.height - centerY)
            ).coerceAtLeast(
                sqrt(centerX * centerX + centerY * centerY)
            ).coerceAtLeast(
                sqrt(centerX * centerX + (size.height - centerY) * (size.height - centerY))
            ).coerceAtLeast(
                sqrt((size.width - centerX) * (size.width - centerX) + centerY * centerY)
            )

            val currentRadius = maxRadius * progress
            val alpha = (1f - progress) * 0.3f

            val color = if (isDark) {
                Color.Black.copy(alpha = alpha)
            } else {
                Color.White.copy(alpha = alpha)
            }

            drawCircle(
                color = color,
                radius = currentRadius,
                center = Offset(centerX, centerY)
            )
        }
    }
}

// Enhanced Theme Toggle with ripple effect
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnhancedThemeToggle(
    isDark: Boolean,
    isTransitioning: Boolean,
    onToggle: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    // Multiple coordinated animations for smoother effect
    val rotationZImpl by animateFloatAsState(
        targetValue = if (isDark) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_rotation"
    )

    val scale by animateFloatAsState(
        targetValue = when {
            isTransitioning -> 1.3f
            isDark -> 1.1f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isTransitioning) 16.dp else 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_elevation"
    )

    // Color animation for the FAB container
    val containerColor by animateColorAsState(
        targetValue = if (isDark)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        else
            MaterialTheme.colorScheme.primaryContainer,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_container_color"
    )

    // Pulsing glow effect during transition
    val glowAlpha by animateFloatAsState(
        targetValue = if (isTransitioning) 0.6f else 0f,
        animationSpec = tween(300),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier.padding(16.dp)
    ) {
        // Glow effect
        if (glowAlpha > 0f) {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier
                    .scale(scale * 1.4f)
                    .graphicsLayer { alpha = glowAlpha },
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                contentColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) { }
        }

        FloatingActionButton(
            onClick = {
                val centerX = buttonPosition.x + with(density) { 28.dp.toPx() }
                val centerY = buttonPosition.y + with(density) { 28.dp.toPx() }
                onToggle(centerX, centerY)
            },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    buttonPosition = coordinates.positionInRoot()
                }
                .graphicsLayer {
                    rotationZ = rotationZImpl
                    scaleX = scale
                    scaleY = scale
                },
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = elevation,
                pressedElevation = elevation + 2.dp,
                focusedElevation = elevation + 1.dp,
                hoveredElevation = elevation + 1.dp
            )
        ) {
            AnimatedContent(
                targetState = isDark,
                transitionSpec = {
                    val enter = fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + scaleIn(
                        initialScale = 0.6f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )

                    val exit = fadeOut(
                        animationSpec = tween(200)
                    ) + scaleOut(
                        targetScale = 0.6f,
                        animationSpec = tween(200)
                    )

                    enter togetherWith exit
                },
                label = "theme_icon_transition"
            ) { dark ->
                val iconScale by animateFloatAsState(
                    targetValue = if (isTransitioning) 0.8f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "icon_scale"
                )

                Icon(
                    imageVector = if (dark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = if (dark) "Switch to Light Mode" else "Switch to Dark Mode",
                    modifier = Modifier.scale(iconScale)
                )
            }
        }
    }
}

@Composable
fun LoadingOverlay(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Text("Loading amazing features...")
            }
        }
    }

    // Автоматически скрыть через 2 секунды
    LaunchedEffect(Unit) {
        delay(2000)
        viewModel.setLoading(false)
    }
}