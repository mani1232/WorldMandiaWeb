package cc.worldmandia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.painterResource
import worldmandiaweb.composeapp.generated.resources.Res
import worldmandiaweb.composeapp.generated.resources.compose_multiplatform

@Composable
fun App() {
    var isDark by rememberSaveable { mutableStateOf(true) }
    var currentTheme = if (isDark) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = currentTheme) {
        Surface(modifier = Modifier.fillMaxSize().background(currentTheme.background)) {
            var showContent by remember { mutableStateOf(false) }
            val greeting = remember { Greeting().greet() }
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(16.dp).zIndex(1f)
            ) {
                FloatingActionButton(
                    onClick = {
                        isDark = !isDark
                        currentTheme = if (isDark) darkColorScheme() else lightColorScheme()
                    }
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                        contentDescription = if (isDark) "Change to Light" else "Change to Dark"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                val status by produceState(initialValue = "Loading...") {
                    startTracking().collect { value = it }
                }

                Text(
                    text = "Your location: $status",
                    modifier = Modifier.padding(20.dp),
                )
                Text(
                    text = "Today's date is ${todayDate()}",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}