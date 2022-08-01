package pl.pjagielski.tracker.player

import kotlinx.coroutines.CoroutineScope
import pl.pjagielski.tracker.model.Note
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

class MidiPlayer(private val receiver: Receiver, notes: List<Note>, metronome: Metronome, scope: CoroutineScope)
    : Player(notes, metronome, scope) {

    override fun playNote(note: Note, playAt: LocalDateTime) {
        println("Playing $note on thread ${Thread.currentThread().name}")
        val midinote = note.midinote
        val midiVel = (127f * note.amp).toInt()
        val noteOnMsg = ShortMessage(ShortMessage.NOTE_ON, 0, midinote, midiVel)
        receiver.send(noteOnMsg, -1)
        val noteOffAt = playAt.plus((note.duration * metronome.millisPerBeat).toLong(), ChronoUnit.MILLIS)
        schedule(noteOffAt) {
            val noteOffMsg = ShortMessage(ShortMessage.NOTE_OFF, 0, midinote, midiVel)
            receiver.send(noteOffMsg, -1)
        }
    }
}
