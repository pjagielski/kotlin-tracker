package pl.pjagielski

import com.sun.nio.file.SensitivityWatchEventModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import kotlin.concurrent.thread

class FileWatcher(private val file: File, private val onChange: (File) -> Unit) {
    private val path: Path = file.absoluteFile.toPath()
    private val parent: Path = path.parent
    private val key: WatchKey = pathKeys.getOrPut(parent) {
        parent.register(
            watchService, arrayOf(StandardWatchEventKinds.ENTRY_MODIFY),
            SensitivityWatchEventModifier.HIGH
        )
    }

    init {
        watchThread
        watching.getOrPut(path) {
            mutableListOf()
        }.add(this)
        keyPaths.getOrPut(key) { parent }
    }

    fun stop() {
        watching[path]?.remove(this)
    }

    internal fun triggerChange() {
        onChange(file)
    }
}

private val watchers = mutableMapOf<() -> Any, FileWatcher>()

fun <T> watchFile(file: File, transducer: (File) -> T): () -> T {
    var result = transducer(file)
    val watcher = FileWatcher(file) {
        try {
            result = transducer(file)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    val function = {
        result
    }

    @Suppress("UNCHECKED_CAST")
    watchers[function as () -> Any] = watcher
    return function
}

private val watching = mutableMapOf<Path, MutableList<FileWatcher>>()
private val pathKeys = mutableMapOf<Path, WatchKey>()
private val keyPaths = mutableMapOf<WatchKey, Path>()
private val waiting = mutableMapOf<Path, Job>()

private val watchService by lazy {
    FileSystems.getDefault().newWatchService()
}

private val watchThread by lazy {
    val scope = CoroutineScope(Dispatchers.IO)
    thread(isDaemon = true) {
        while (true) {
            val key = watchService.take()
            val path = keyPaths[key]
            key.pollEvents().forEach { event ->
                val contextPath = event.context() as Path
                val fullPath = path?.resolve(contextPath)

                fullPath?.let {
                    waiting[fullPath]?.cancel()

                    waiting[fullPath] = scope.launch {
                        delay(100)
                        watching[fullPath]?.forEach { w ->
                            w.triggerChange()
                        }
                    }
                }
            }
            key.reset()
        }
    }
}
