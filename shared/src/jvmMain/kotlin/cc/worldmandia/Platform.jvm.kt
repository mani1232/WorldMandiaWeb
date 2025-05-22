package cc.worldmandia

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
actual suspend fun getLocation(): String? {
    TODO("Not yet implemented")
}