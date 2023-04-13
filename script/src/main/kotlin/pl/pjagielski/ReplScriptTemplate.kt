package pl.pjagielski

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogger
import org.slf4j.Logger
import java.net.http.HttpClient
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
    fileExtension = "repl.kts",
    compilationConfiguration = ReplScriptCompilationConfiguration::class
)
abstract class ReplScriptTemplate(context: ReplContext) : ReplContext by context

interface ReplContext {
    val log: KLogger
    val objectMapper: ObjectMapper
    val client: HttpClient
    var result: Any?
}

object ReplScriptCompilationConfiguration : ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

