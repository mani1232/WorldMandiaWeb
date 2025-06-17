package cc.worldmandia

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    hideLoader()

    CanvasBasedWindow { startCompose() }
}

private fun hideLoader() {
    val loadingContainer = document.getElementById("loading-container")
    val composeCanvas = document.getElementById("ComposeTarget")

    loadingContainer?.classList?.add("hidden")
    composeCanvas?.classList?.add("visible")

    loadingContainer?.addEventListener("transitionend") {
        loadingContainer.remove()
    }
    document.title = "WorldMandiaWeb"
}