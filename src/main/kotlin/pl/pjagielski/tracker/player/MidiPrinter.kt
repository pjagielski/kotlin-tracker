package pl.pjagielski.tracker.player

import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

class MidiPrinter

fun main() {

    val sequencer = MidiSystem.getSequencer()
    sequencer.setSequence(MidiPrinter::class.java.getResourceAsStream("/GiorgiobyMoroder.mid"))
//    sequencer.setSequence(MidiPrinter::class.java.getResourceAsStream("/Levels.mid"))

    val sequence = sequencer.sequence

    println("Division type: ${sequence.divisionType}, resolution: ${sequence.resolution}")

    sequence.tracks.forEachIndexed { index, track ->
        println("Track $index")
        (0 until track.size()).asSequence().map { idx ->
            val event = track[idx]
//            println("Tick ${event.tick}, event: $event")
//            println("Tick ${event.tick}, message: ${event.message}")
            when (val message = event.message) {
                is ShortMessage -> {
                    val command = message.command
                    val data1 = message.data1
                    val data2 = message.data2
                    println("Tick ${event.tick}, command: $command, data1: $data1, data2: $data2")
                }
            }
        }
            .take(16)
            .toList()
    }
}
