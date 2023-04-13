package pl.pjagielski.player

import com.illposed.osc.OSCMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import pl.pjagielski.punkt.Metronome
import pl.pjagielski.punkt.jam.midiToHz
import pl.pjagielski.punkt.osc.OscServer
import pl.pjagielski.punkt.sounds.Samples
import java.time.LocalDateTime

interface Note {
    val beat: Double
    val amp: Float
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
    private val notes: List<Note>, private val metronome: Metronome, private val scope: CoroutineScope
) {
    abstract fun playNote(note: Note, playAt: LocalDateTime)
}

class OSCPlayer(
    val oscServer: OscServer, val samples: Samples,
    notes: List<Note>, metronome: Metronome, scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Player(notes, metronome, scope) {

    override fun playNote(note: Note, playAt: LocalDateTime) {
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
                oscServer.sendInBundle(listOf(packet), playAt)
            }
        }
    }
}
