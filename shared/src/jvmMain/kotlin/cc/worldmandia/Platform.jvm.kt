package cc.worldmandia

import kotlinx.coroutines.flow.Flow

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
actual suspend fun startTracking(): Flow<String> {
    TODO("Not yet implemented")
}