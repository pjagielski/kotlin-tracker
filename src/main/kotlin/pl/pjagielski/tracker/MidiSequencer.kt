package pl.pjagielski.tracker

import kotlinx.coroutines.runBlocking
import pl.pjagielski.tracker.player.Metronome
import pl.pjagielski.tracker.player.MidiPlayer
import java.io.File
import java.io.FileWriter
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import javax.sound.midi.MidiSystem

class MidiSequencer

fun main() {
    val sequencer = MidiSystem.getSequencer()
    sequencer.setSequence(MidiSequencer::class.java.getResourceAsStream("/GiorgiobyMoroder.mid"))

    val synthesiser = MidiSystem.getSynthesizer()
    synthesiser.open()

//    val midiInstr = 1 // Acoustic Grand Piano
//    val midiInstr = 38 // Slap Bass
//    val midiInstr = 6 // Chorused Piano
//    val midiInstr = 17 // Hammond
    val midiInstr = 30 // Overdriven Guitar

    val currentInstrument = synthesiser.availableInstruments[midiInstr]
    synthesiser.loadInstrument(currentInstrument)
    synthesiser.channels[0].programChange(currentInstrument.patch.bank, currentInstrument.patch.program)

    val sequence = sequencer.sequence
    val beats = 16

    val melody = sequence.toNotes().takeWhile { it.beat < beats }
    val metronome = Metronome(beatsPerBar = beats, bpm = 90)

    runBlocking {
//        val device = midiDevice("VirMIDI")
//        val receiver = device.receiver
        val receiver = synthesiser.receiver

        val player = MidiPlayer(receiver, melody, metronome, this)
//        player.playNotes(LocalDateTime.now())
        player.playBar(1, LocalDateTime.now())

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Stopping player...")
            runBlocking {
                player.stop()
//                device.close()
            }
        })
    }
}

private fun midiDevice(desc: String) =
    MidiSystem.getMidiDeviceInfo().toList()
        .map { MidiSystem.getMidiDevice(it) }
        .first { it.deviceInfo.description.startsWith(desc) }
        .apply { open() }
