package pl.pjagielski.helper

import pl.pjagielski.player.Note
import java.math.RoundingMode
import javax.sound.midi.ShortMessage

fun javax.sound.midi.Sequence.toNotes(tracksToPlay: List<Int>): List<Note> {
    return tracksToPlay.flatMap { trackIdx ->
        val track = tracks[trackIdx]
        (0 until track.size()).asSequence().map { idx ->
            val event = track[idx]
            when (val message = event.message) {
                is ShortMessage -> {
                    val command = message.command
                    val midinote = message.data1
                    val amp = message.data2
                    val beat = (event.tick / resolution.toDouble())
                        .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    if (command == ShortMessage.NOTE_ON) {
                        val duration = 0.25 // lazy ;)
                        return@map Note(beat, midinote, duration, amp / 127.0f)
                    }
                }
            }
            null
        }.filterNotNull()
    }
}

fun javax.sound.midi.Sequence.toNotesPreservingDuration(tracksToPlay: List<Int>): List<Note> {
    return tracksToPlay.flatMap { trackIdx ->
        val track = tracks[trackIdx]
        val inflight = mutableMapOf<Int, Note>()
        (0 until track.size()).asSequence().map { idx ->
            val event = track[idx]
            when (val message = event.message) {
                is ShortMessage -> {
                    val command = message.command
                    val midinote = message.data1
                    val amp = message.data2
                    val beat = (event.tick / resolution.toDouble())
                        .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    when (command) {
                        ShortMessage.NOTE_ON -> {
                            val note = Note(beat, midinote, 0.25, amp / 127.0f)
                            inflight[midinote] = note
                        }
                        ShortMessage.NOTE_OFF -> {
                            val note = inflight.remove(midinote)
                            return@map note?.let { it.copy(duration = beat - it.beat) }
                        }
                    }
                }
            }
            null
        }.filterNotNull()
    }
}
