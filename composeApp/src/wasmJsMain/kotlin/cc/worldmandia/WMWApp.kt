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
    var currentTheme by mutableStateOf(lightColorScheme())

    suspend fun updateTheme(storage: KStore<SavedState>) {
        storage.update {
            it?.copy(isDarkTheme = !it.isDarkTheme).also { newState ->
                currentTheme = if (newState?.isDarkTheme ?: false) darkColorScheme() else lightColorScheme()
            }
        }
    }
}

@Serializable
data class SavedState(
    var isDarkTheme: Boolean = false,
)

@Composable
fun startCompose() {
    KoinApplication(application = {
        modules(
            module {
                single<KStore<SavedState>> { storeOf(key = "saved_state", default = SavedState()) }
            },
        )
    }) {
        MaterialTheme(
            colorScheme = CurrentState.currentTheme,
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
    var isDark by mutableStateOf(false)

    FloatingActionButton(
        onClick = {
            scope.launch {
                CurrentState.updateTheme(storage)
                isDark = storage.get()?.isDarkTheme ?: false
            }

        },
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = isDark,
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