package cc.worldmandia

import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.BrowserGeolocator

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual suspend fun getLocation(): String? {
    return BrowserGeolocator().current(Priority.HighAccuracy).getOrNull()?.let {
        "LT${it.coordinates.latitude} and LG${it.coordinates.longitude}"
    }
}