package pl.pjagielski

import javax.sound.midi.MidiSystem

class JvmMidiSequencer

fun main() {
//    val file = "GiorgiobyMoroder.mid"
//    val file = "Don't_You_Worry_Child.mid"
    val file = "Still_Dre.mid"

    val midiStream = JvmMidiSequencer::class.java.getResourceAsStream("/$file")
    val sequencer = MidiSystem.getSequencer().apply { setSequence(midiStream) }
    sequencer.open()
    sequencer.start()
}
