package github.coderpwh.plugins

import github.coderpwh.client.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    routing {
        var client = DyClient()
        get("/download") {
            var url = call.request.queryParameters["url"]!!
            if (url.trim().isEmpty()) {
                call.respond("url param is null")
            }
            var fileName = call.request.queryParameters["fileName"]!!
            var vUrl = client.exec(url)
            if (vUrl.isEmpty()) {
                call.respond("解析失败")
            }
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileName).toString()
            )
            call.respondBytes {
                ->
                client.getByteArray(vUrl)
            }
        }
    }
}
