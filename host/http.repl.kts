import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

log.info { "Hello from script" }

val request: HttpRequest = HttpRequest.newBuilder()
    .uri(URI("https://api.github.com/repos/jetbrains/kotlin"))
    .GET()
    .build()

val rawResponse: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

val parsedResponse: Map<*, *> = objectMapper.readValue(rawResponse.body(), Map::class.java)

//result = parsedResponse
val parsedOwner = parsedResponse["owner"] as Map<*, *>
log.info { parsedOwner }
//result = parsedOwner

//result = listOf(parsedResponse["full_name"], parsedResponse["stargazers_count"], parsedOwner)
result = listOf(parsedResponse["full_name"], parsedResponse["stargazers_count"], parsedOwner["login"])
