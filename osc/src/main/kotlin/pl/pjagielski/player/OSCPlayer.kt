package pl.pjagielski

import com.illposed.osc.OSCMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.pjagielski.punkt.jam.Player
import pl.pjagielski.punkt.osc.OscServer
import pl.pjagielski.punkt.pattern.Sample
import pl.pjagielski.punkt.pattern.Synth
import pl.pjagielski.punkt.sounds.Samples
import pl.pjagielski.player.Note
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface Note {
    val beat: Double
}

data class Synth(
    override val beat: Double,
    val name: String,
    val midinote: Int,
    val duration: Double,
    override val amp: Float = 1.0f
) : Note

data class Sample(
    override val beat: Double,
    val name: String,
    override val amp: Float = 1.0f
) : Note

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

class OSCPlayer(val oscServer: OscServer, val samples: Samples, notes: List<Note>) : Player(state) {
    override fun playNote(note: Note, playAt: LocalDateTime, state: State) {
        println("Playing $note")
        when (note) {
            is Synth -> {
                val midinote = note.midinote
                val freq = midiToHz(midinote)
                val dur = note.duration.toFloat()
                val params = listOf("freq", freq, "amp", note.amp, "dur", dur)
                val packet = OSCMessage("/s_new", listOf(note.name, -1, 0, 0) + params)
                oscServer.sendInBundle(listOf(packet), runAt = playAt)
            }
            is Sample -> {
                val meta = samples[note.name]
                if (meta == null) {
                    println("Unknown sample ${note.name}")
                    return
                }
                val synth = "play${meta.channels}"
                val packet = OSCMessage("/s_new", listOf(synth, -1, 0, 0, "buf", meta.bufNum, "amp", note.amp))
                oscServer.send(packet, playAt)
            }
        }
    }
}
