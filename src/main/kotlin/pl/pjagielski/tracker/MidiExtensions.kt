package pl.pjagielski.tracker

import pl.pjagielski.tracker.model.Note
import java.math.RoundingMode
import javax.sound.midi.ShortMessage

fun javax.sound.midi.Sequence.toNotes(tracksToPlay: List<Int>, tickOffset: Int = 0): List<Note> {
    return tracksToPlay.flatMap { trackIdx ->
        val track = tracks[trackIdx]
        (0 until track.size()).asSequence().map { idx ->
            val event = track[idx]
            when (val message = event.message) {
                is ShortMessage -> {
                    val command = message.command
                    val midinote = message.data1
                    val amp = message.data2
                    if (command == ShortMessage.NOTE_ON) {
                        val beat = ((event.tick - tickOffset) / resolution.toDouble())
                            .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                            .toDouble()
                        return@map Note(beat, midinote, 0.25, amp / 127.0f)
                    }
                }
            }
            null
        }.filterNotNull()
    }
}

fun javax.sound.midi.Sequence.toNotes(): List<Note> {
    return tracks.flatMap { track ->
        (0 until track.size()).asSequence().map { idx ->
            val event = track[idx]
            when (val message = event.message) {
                is ShortMessage -> {
                    val command = message.command
                    val midinote = message.data1
                    val amp = message.data2
                    if (command == ShortMessage.NOTE_ON) {
                        val beat = (event.tick / resolution.toDouble())
                            .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                            .toDouble()
                        return@map Note(beat, midinote, 0.25, amp / 127.0f)
                    }
                }
            }
            null
        }.filterNotNull()
    }
}
