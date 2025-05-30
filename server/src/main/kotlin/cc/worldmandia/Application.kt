package cc.worldmandia

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import java.nio.file.Files
import kotlin.io.path.Path

const val filesFolder = "static/"

fun main() {
    Files.createDirectories(Path(filesFolder))
    embeddedServer(CIO, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        singlePageApplication {
            useResources = true
            filesPath = filesFolder
            defaultPage = "index.html"
            ignoreFiles {
                it.endsWith(".txt")
            }
        }
        get("/signin-steam") {
            val params = call.request.queryParameters

            val state = params["state"]

            val openidMode = params["openid.mode"]
            val openidClaimedId = params["openid.claimed_id"]

            call.respondText("""
                    State: $state
                    openid.mode: $openidMode
                    openid.claimed_id: $openidClaimedId
                    Все параметры:
                    ${params.entries().joinToString("\n") { "${it.key} = ${it.value.joinToString()}" }}
                """.trimIndent())
        }
    }
}