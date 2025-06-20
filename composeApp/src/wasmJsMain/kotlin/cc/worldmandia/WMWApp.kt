package cc.worldmandia

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module


object CurrentState {

    object ThemeState {

        var currentTheme by mutableStateOf(lightColorScheme())
        var currentThemeState by mutableStateOf(ThemeSelectionType.LOCAL)

        fun isDark() = currentThemeState == ThemeSelectionType.DARK

        suspend fun updateTheme(storage: KStore<SavedState>) {
            currentThemeState = when (currentThemeState) {
                ThemeSelectionType.LIGHT -> {
                    ThemeSelectionType.DARK
                }

                ThemeSelectionType.DARK -> {
                    ThemeSelectionType.LIGHT
                }

                ThemeSelectionType.LOCAL -> if (storage.get()?.isDarkTheme ?: false) {
                    ThemeSelectionType.DARK
                } else {
                    ThemeSelectionType.LIGHT
                }
            }
            currentTheme = if (isDark()) darkColorScheme() else lightColorScheme()
            storage.update {
                it?.copy(isDarkTheme = currentThemeState == ThemeSelectionType.DARK)
            }
        }
    }
}

enum class ThemeSelectionType {
    LIGHT, DARK, LOCAL
}

@Serializable
data class SavedState(
    var isDarkTheme: Boolean = false,
)

@Composable
fun startCompose() {
    val scope = rememberCoroutineScope()

    KoinApplication(application = {
        modules(
            module {
                single<KStore<SavedState>> {
                    val storage = storeOf(key = "saved_state", default = SavedState(), enableCache = true)
                    scope.launch {
                        CurrentState.ThemeState.updateTheme(storage)
                    }
                    storage
                }
            },
        )
    }) {
        MaterialTheme(
            colorScheme = CurrentState.ThemeState.currentTheme,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                mainPage()
            }
        }
    }
}

@Composable
fun mainPage() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Содержимое вашего приложения",
            modifier = Modifier.align(Alignment.Center)
        )

        ThemeToggleButton(
            modifier = Modifier
                .padding(16.dp)
                .zIndex(10f)
                .align(Alignment.BottomEnd)
        )
    }
}

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun ThemeToggleButton(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val storage: KStore<SavedState> = koinInject()
    scope.launch {
        storage.get() // Preload
    }

    FloatingActionButton(
        onClick = {
            scope.launch {
                CurrentState.ThemeState.updateTheme(storage)
            }
        },
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = CurrentState.ThemeState.isDark(),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { isDarkIcon ->
            if (isDarkIcon) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Переключить на светлую тему"
                )
            } else {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Переключить на темную тему"
                )
            }
        }
    }
}