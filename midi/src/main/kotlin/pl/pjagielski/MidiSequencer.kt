package pl.pjagielski

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import pl.pjagielski.helper.toNotes
import pl.pjagielski.helper.toNotesPreservingDuration
import pl.pjagielski.player.Metronome
import pl.pjagielski.player.MidiPlayer
import pl.pjagielski.player.Note
import java.time.LocalDateTime
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

class MidiSequencer

fun main() {
    val sequencer = MidiSystem.getSequencer()
//    val (track, beats, from, bpm) = listOf(0, 16, 0, 100)
//    sequencer.setSequence(MidiSequencer::class.java.getResourceAsStream("/GiorgiobyMoroder.mid"))
//    val (track, beats, from, bpm) = listOf(2, 8, 4, 85)
//    sequencer.setSequence(MidiSequencer::class.java.getResourceAsStream("/Still_Dre.mid"))
    val (track, beats, from, bpm) = listOf(4, 64, 16, 129)
    sequencer.setSequence(MidiSequencer::class.java.getResourceAsStream("/Don't_You_Worry_Child.mid"))

    val synthesiser = MidiSystem.getSynthesizer().apply { open() }

//    val midiInstr = 1 // Acoustic Grand Piano
//    val midiInstr = 38 // Slap Bass
//    val midiInstr = 6 // Chorused Piano
//    val midiInstr = 17 // Hammond
    val midiInstr = 30 // Overdriven Guitar

    val currentInstrument = synthesiser.availableInstruments[midiInstr]
    synthesiser.loadInstrument(currentInstrument)
    synthesiser.channels[0].programChange(currentInstrument.patch.bank, currentInstrument.patch.program)

    val sequence = sequencer.sequence

    fun List<Note>.takeFromBeat(beat: Int) = this
        .takeLastWhile { it.beat >= beat }
        .map { it.copy(beat = it.beat - beat) }

    val melody = sequence.toNotesPreservingDuration(listOf(track))
        .filter { it.amp > 0 }
        .takeFromBeat(from)
        .takeWhile { it.beat < beats }

    val metronome = Metronome(beatsPerBar = beats, bpm = bpm)

    runBlocking(Dispatchers.IO) {
//        val device: MidiDevice? = null
        val device = midiDevice("VirMIDI")
        val receiver = device.receiver
//        val receiver = synthesiser.receiver

        val player = MidiPlayer(receiver, melody, metronome, this)
        player.start()

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Stopping player...")
            runBlocking {
                player.stop()
                receiver.close()
                device?.close()
            }
        })
    }
}

private fun midiDevice(desc: String) =
    MidiSystem.getMidiDeviceInfo().toList()
        .map { MidiSystem.getMidiDevice(it) }
        .first { it.deviceInfo.description.startsWith(desc) }
        .apply { open() }
