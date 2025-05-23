package cc.worldmandia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cc.worldmandia.AppData.isDark
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource
import org.w3c.dom.get
import org.w3c.dom.set
import worldmandiaweb.composeapp.generated.resources.Res
import worldmandiaweb.composeapp.generated.resources.compose_multiplatform

object AppData {
    var isDark = mutableStateOf(false)
}

@Composable
fun App() {
    isDark.value = window.localStorage["isDark"]?.toBoolean() ?: false
    MaterialTheme(colorScheme = if (isDark.value) darkColorScheme() else lightColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            themeSelector()
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var showContent by remember { mutableStateOf(false) }
                val greeting = remember { Greeting().greet() }
                // TODO Location getter and updater
                //val status by produceState(initialValue = "Loading...") {
                //    startTracking().collect { value = it }
                //}
                //Text(
                //    text = "Your location: $status",
                //    modifier = Modifier.padding(20.dp),
                //)
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

@Composable
fun themeSelector() {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.padding(16.dp).zIndex(1f)
    ) {
        FloatingActionButton(
            onClick = {
                isDark.value = !isDark.value
                window.localStorage["isDark"] = (isDark.value).toString()
            }
        ) {
            Icon(
                imageVector = if (isDark.value) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                contentDescription = if (isDark.value) "Change to Light" else "Change to Dark"
            )
        }
    }
}