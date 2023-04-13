package pl.pjagielski.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

abstract class Player(
    private val notes: List<Note>, protected val metronome: Metronome,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private var playing = true

    fun start() {
        playing = true
        playBar(0, LocalDateTime.now())
    }

    suspend fun stop() {
        playing = false
        delay(metronome.millisPerBeat)
    }

    fun playBar(bar: Int, at: LocalDateTime) {
        if (!playing) return
        println("Playing bar $bar")
        playNotes(at)
        val nextBarAt = at.plus(metronome.millisPerBar, ChronoUnit.MILLIS)
        schedule(nextBarAt) {
            playBar(bar + 1, nextBarAt)
        }
    }

    fun playNotes(at: LocalDateTime) {
        notes.forEach { note ->
            val playAt = at.plus((note.beat * metronome.millisPerBeat).toLong(), ChronoUnit.MILLIS)
            schedule(playAt) {
                if (playing) {
                    playNote(note, playAt)
                }
            }
        }
    }

    abstract fun playNote(note: Note, playAt: LocalDateTime)

    fun schedule(time: LocalDateTime, function: () -> Unit) {
        scope.launch {
            delay(Duration.between(LocalDateTime.now(), time).toMillis())
            function.invoke()
        }
    }
}
