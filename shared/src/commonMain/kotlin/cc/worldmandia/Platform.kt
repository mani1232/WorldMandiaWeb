package cc.worldmandia

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun getLocation(): String?