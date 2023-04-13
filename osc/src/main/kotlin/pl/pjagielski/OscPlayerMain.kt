package pl.pjagielski

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import pl.pjagielski.punkt.application
import pl.pjagielski.punkt.config.Configuration
import pl.pjagielski.punkt.config.TrackConfig
import pl.pjagielski.punkt.fx.chop
import pl.pjagielski.punkt.fx.dist
import pl.pjagielski.punkt.fx.djf
import pl.pjagielski.punkt.fx.krush
import pl.pjagielski.punkt.fx.waveDist
import pl.pjagielski.punkt.jam.StateProvider
import pl.pjagielski.punkt.param.LFO
import pl.pjagielski.punkt.pattern.Note
import pl.pjagielski.punkt.pattern.Synth
import pl.pjagielski.punkt.pattern.TimeVarMidiNote
import pl.pjagielski.punkt.pattern.amp
import pl.pjagielski.punkt.pattern.cycle
import pl.pjagielski.punkt.pattern.every
import pl.pjagielski.punkt.pattern.loop
import pl.pjagielski.punkt.pattern.mute
import pl.pjagielski.punkt.pattern.params
import pl.pjagielski.punkt.pattern.patterns
import pl.pjagielski.punkt.pattern.repeat
import pl.pjagielski.punkt.pattern.sample
import pl.pjagielski.punkt.pattern.track
import java.math.RoundingMode
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

class OscPlayerMain

fun main() {
    val stateProvider = object: StateProvider() {

        override fun provide(config: TrackConfig): List<Note> {
//            config.tracks[0].djf(LFO(0.3, 0.85, 8))
            config.tracks[0].djf(0.0)

            config.tracks[1].delay(level = 0.5, echo = 0.5, echotime = 0.75)

            config.tracks[3].reverb(level = 0.75, drywet = 0.5, damp = 0.5)
            config.tracks[3].delay(level = 0.5, echo = 0.75, echotime = 2.5)

            val sequencer = MidiSystem.getSequencer()

//            val (track, beats, from, ll) = listOf(0, 64, 0, 4)
//            val file = "/GiorgiobyMoroder.mid"
            val (track, beats, from, ll) = listOf(4, 16, 16, 4)
            val file = "/Don't_You_Worry_Child.mid"
            val (bd, sd, hh, hats) = listOf("bd", "sd", "hh", "mk-hats")

            sequencer.setSequence(OscPlayerMain::class.java.getResourceAsStream(file))
            val sequence = sequencer.sequence

            return patterns(beats = beats) {

                + sequence.toNotes(listOf(track))
                    .filter { it.amp > 0 }
                    .takeFromBeat(from)
                    .asSequence()
                    .params("timbre" to 0.1)
                    .params("morph" to LFO(0.3, 0.75, 8))
                    .params("harm" to 0.3)
                    .params("sus" to 0.99, "atk" to 0.01)
                    .params("pan" to LFO(0.75, -0.75, 8))
                    .params("engine" to 1)
                    .track(3)
                    .chop(config, 2)
                    .waveDist(LFO(0.25, 0.5, 8))
                    .dist(LFO(0.3, 0.7, 8))
                    .djf(LFO(0.45, 0.35, 6))
//                    .mute()

                + sequence.toNotes(listOf(track), synth = "bass8")
                    .filter { it.amp > 0 }
                    .takeFromBeat(from)
                    .asSequence()
                    .map { it.copy(midinote = it.midinote.low()) }
                    .every(3, { null }, from = 0)
                    .every(2, { null }, from = 0)
                    .amp(0.75)
                    .track(1)
                    .dist(0.15)
                    .djf(LFO(0.35, 0.45, 6))

                + sequence.toNotes(listOf(track), synth = "lead")
                    .filter { it.amp > 0 }
                    .takeFromBeat(from)
                    .asSequence()
                    .track(3)
                    .waveDist(0.2)
                    .djf(LFO(0.45, 0.35, 6))
                    .chop(config, 1)
//                    .mute()

                + cycle(1.25, 0.75, 2.0)
                    .sample(bd)
                    .amp(0.75)
//                    .mute()

                + repeat(2.0).sample(sd, at = 1.0)
                    .amp(0.85)
//                    .mute()

                + listOf((0.25 to 2), (0.5 to 4), (0.25 to 4))
                    .flatMap { (dur, i) -> (1..i).map { dur } }
                    .let { cycle(it) }
                    .sample(hh)
                    .chop(config, 4)
                    .amp(cycle(0.5, 0.75, 0.5))

                + repeat(ll).loop(hats, ll)
                    .amp(0.25)
                    .mute()

                + cycle(ll).loop("mk-vox-A", ll)
                    .dist(0.25)
                    .mute()

                + cycle(ll).loop("echoes-beat", ll, startBeat = 4)
                    .amp(0.75)
//                    .chop(config, 1)
                    .mute()
            }
        }
    }
    application(
        config = Config { addSpec(Configuration) }.from.yaml.resource("config.yaml"),
        stateProvider = stateProvider
    )
}

fun List<Synth>.takeFromBeat(beat: Int) = this
    .takeLastWhile { it.beat >= beat }
    .map { it.copy(beat = it.beat - beat) }


fun javax.sound.midi.Sequence.toNotes(tracksToPlay: List<Int>, tickOffset: Int = 0, synth: String = "plaits"): List<Synth> {
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
                        return@map Synth(beat, 0.25, synth, TimeVarMidiNote(midinote), amp / 127.0f)
                    }
                }
            }
            null
        }.filterNotNull()
    }
}
