package pl.pjagielski.helper

import pl.pjagielski.JvmMidiSequencer
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

fun main() {
    val file = "Don't_You_Worry_Child.mid"

    val midiStream = JvmMidiSequencer::class.java.getResourceAsStream("/$file")
    val sequencer = MidiSystem.getSequencer().apply { setSequence(midiStream) }

    sequencer.sequence.tracks.forEachIndexed { index, track ->
        println("Track $index")
        (0 until track.size()).asSequence().forEach { idx ->
            val event = track[idx]
//            println("Tick ${event.tick}, message: ${event.message}")
            when (val message = event.message) {
                is ShortMessage -> {
                    val command = message.command
                    val data1 = message.data1
                    val data2 = message.data2
                    println("Tick ${event.tick}, command: $command, data1: $data1, data2: $data2")
                }
                else -> {}
            }
        }
    }

}
