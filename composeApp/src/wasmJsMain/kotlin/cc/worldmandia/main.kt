package cc.worldmandia

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    hideHtmlLoader()

    CanvasBasedWindow {
        App()
    }
}

@JsName("hideLoader")
external fun hideHtmlLoader()