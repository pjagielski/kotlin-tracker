package pl.pjagielski.tracker

import javax.sound.midi.MidiSystem

class JvmMidiSequencer

fun main() {

    val midiStream = JvmMidiSequencer::class.java.getResourceAsStream("/GiorgiobyMoroder.mid")
    val sequencer = MidiSystem.getSequencer().apply { setSequence(midiStream) }
    sequencer.open()
    sequencer.start()

}
