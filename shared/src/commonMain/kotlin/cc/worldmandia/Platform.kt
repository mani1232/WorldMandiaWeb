package cc.worldmandia

import kotlinx.coroutines.flow.Flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun startTracking(): Flow<String>