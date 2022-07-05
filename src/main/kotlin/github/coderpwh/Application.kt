package github.coderpwh

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import github.coderpwh.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation)
        configureRouting()
    }.start(wait = true)
}
