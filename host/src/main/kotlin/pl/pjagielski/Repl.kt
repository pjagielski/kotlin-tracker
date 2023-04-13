package pl.pjagielski

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import java.io.File
import java.net.http.HttpClient
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

fun main() {

    val log = KotlinLogging.logger { }

    val host = BasicJvmScriptingHost()

    val context = object : ReplContext {
        override val log = log
        override val objectMapper = jacksonObjectMapper()
        override val client = HttpClient.newHttpClient()
        override var result: Any? = null
    }

    watchFile(File("host/http.repl.kts")) { file ->
        val start = System.currentTimeMillis()
        log.info("Reloading file...")

        host.evalWithTemplate<ReplScriptTemplate>(
            script = file.toScriptSource(),
            evaluation = {
                constructorArgs(context)
            }
        ).valueOrThrow()

        val stop = System.currentTimeMillis()
        log.info("Script took ${stop - start}ms")
        log.info("Script returned ${context.result}")
    }

    Thread.currentThread().join()
}
