package cc.worldmandia

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.nio.file.Files
import kotlin.io.path.Path

const val filesFolder = "compose-application/"

fun main() {
    Files.createDirectories(Path(filesFolder))
    embeddedServer(CIO, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        singlePageApplication {
            useResources = false
            filesPath = filesFolder
            defaultPage = "index.html"
            ignoreFiles {
                it.endsWith(".txt")
            }
        }
    }
}