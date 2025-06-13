package cc.worldmandia

import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.BrowserGeolocator
import dev.jordond.compass.geolocation.TrackingStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val geolocation = BrowserGeolocator()

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual suspend fun startTracking(): Flow<String> {
    geolocation.startTracking()
    val last = geolocation.lastLocation().getOrNull()
    return geolocation.trackingStatus.map { status ->
        when (val s = status) {
            is TrackingStatus.Error -> "Error"
            TrackingStatus.Idle -> "Idle: ${locationToString(last)}"
            TrackingStatus.Tracking -> "Tracking: ${locationToString(last)}"
            is TrackingStatus.Update -> {
                locationToString(s.location)
            }
        }
    }
}

fun locationToString(loc: Location?): String {
    return if (loc != null) {
        "LT${loc.coordinates.latitude} and LG${loc.coordinates.longitude} Speed: ${loc.speed}"
    } else {
        "null"
    }
}