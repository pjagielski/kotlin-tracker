@file:Suppress("UNUSED_LAMBDA_EXPRESSION")

import pl.pjagielski.punkt.config.TrackConfig
import pl.pjagielski.punkt.fx.*
import pl.pjagielski.punkt.melody.*
import pl.pjagielski.punkt.melody.Intervals.minor
import pl.pjagielski.punkt.param.LFO
import pl.pjagielski.punkt.pattern.*

{ config: TrackConfig ->

    fun Sequence<Step>.tb303() = synth("tb303")
        .params("wave" to 0)
        .params("pan" to LFO(-0.75, 0.75, 6))

    fun Sequence<Step>.plaits() = synth("plaits")
        .params("harm" to 0.5)
        .params("morph" to 0.7)
        .params("timbre" to 0.3)
//        .params("morph" to 0.3)
//        .params("harm" to 0.3)
        .params("sus" to 0.99, "atk" to 0.01)
        .params("pan" to LFO(0.75, -0.75, 8))
        .params("engine" to 3)

    val scale = Scale(A, minor)

//    config.tracks[0].djf(LFO(0.9, 0.7, 4))
    config.tracks[0].djf(0.5)
    config.tracks[0].comp(0.5)

    config.tracks[1].delay(level = 0.5, echo = 0.75, echotime = 6.0)
    config.tracks[1].reverb(level = 0.1, drywet = 0.3, damp = 0.2)
    config.tracks[1].djf(0.5)
    config.tracks[1].comp(0.5)
//    config.tracks[1].djf(LFO(0.3, 0.6, 4))

    config.tracks[2].delay(level = 0.05, echo = 0.5, echotime = 1.5)
    config.tracks[2].reverb(level = 0.05, drywet = 0.5, damp = 0.2)
//    config.tracks[2].djf(LFO(0.7, 0.89, 2))
    config.tracks[2].djf(0.5)
    config.tracks[2].comp(0.7)
//    config.tracks[2].djf(LFO(0.5, 0.2, 6))

    config.tracks[3].delay(level = 0.25, echo = 1.25, echotime = 2.0)
    config.tracks[3].reverb(level = 0.9, drywet = 0.2, damp = 0.1)
//    config.tracks[3].djf(LFO(0.99999, 0.8, 5))
    config.tracks[3].djf(0.5)
    config.tracks[3].comp(0.5)

    val beats = 8
    val (bd, sd, hh, tops, beat) = listOf("bd", "sd", "hh", "mk-hats", "dl-soundcheck")

    config.beatsPerBar = beats
    config.bpm = 120

    patterns(beats = beats) {

        val cl = 2
        + repeat(cl).loop("crowd-party", cl, startBeat = 1)
//            .waveDist(0.7)
            .amp(0.75)
//            .chop(config, 1)
//            .track(1)
//            .mute()

        + cycle(4).sample("devs", at = 0.75)
            .amp(0.75)
            .dist(0.25)
            .track(2)
            .djf(LFO(0.8, 0.6, 6))
            .mute()

        val progression = listOf(
            Chord.I.inversion(1), // -> Am
            Chord.V.sus4().inversion(2).low(), // -> Emsus4
            Chord.V.inversion(2).low() // -> Em
        )

        val (Am, Emsus4, Em) = progression

        fun Step.offset(offset: Double) = this.copy(beat = this.beat + offset)

        + scale
            .high()
            .phrase(
                chords(
                    cycle(
                        (Am to 8),
                        (Emsus4 to 3),
                        (Em to 5)
                    )
                        .flatMap { (ch, i) -> (1..i).map { ch } }
//                        .flatMap { listOf(it, null) }
                ),
//                repeat(0.5)
                repeat(0.75)
            )
            .every(3, { it.offset(0.04) }, from = 1)
            .every(3, { it.offset(0.09) }, from = 2)
            .synth("piano")
            .track(3)
//            .waveDist(0.2)
            .amp(0.75)
//            .djf(0.3)
            .mute()

        val (a, b, e) = listOf(0, 1, -3)
        fun Scale.bassline() =
            this.low().low().phrase(
                // -> A, B -> E, E ->
                degrees(listOf(a, null, b, null, e, null, e)),
                cycle(2.0, 1.0, 0.75, 0.25)
            )

        + scale
            .high()
            .bassline()
            .synth("bass8")
            .amp(0.75)
            .track(1)
            .dist(0.5)
            .chop(config, cycle(0.5, 1, 1))
//            .mute()

        + scale
            .high()
            .bassline()
            .synth("piano")
            .track(1)
            .amp(0.5)
            .dist(0.25)
            .chop(config, 1)
//            .mute()

        + scale.low()
            .phrase(
                degrees(
                    cycle(progression)
                        .flatMap { arp(it, 3, ArpType.UPDOWN) }
//                        .flatMap { listOf(it, null) }
                ),
//                cycle(0.75, 0.5)
                cycle(0.25)
            )
//            .every(3, Step::low, 1)
//            .every(5, Step::rest, 3)
            .tb303()
            .params("res" to 0.25, "wave" to 0)
            .params("start" to 200)
            .params("cutoff" to LFO(2500, 1500, 8))
            .track(2)
            .chop(config, cycle(1))
            .waveDist(0.25)
//            .krush(2, 2, 0.5, 0.25)
            .djf(LFO(0.55, 0.35, 6))
//            .mute()

        + scale.low()
            .phrase(
                degrees(
                    cycle(progression.reversed())
//                    cycle(progression)
                        .flatMap { arp(it, 4, ArpType.UPDOWN) }
                        .flatMap { listOf(it, null) }
                ),
                cycle(0.5, 0.25)
            )
            .every(3, Step::low, 1)
//            .every(3, Step::rest, 3)
            .plaits()
            .track(2)
            .waveDist(0.5)
            .dist(0.5)
//            .krush(2, 2, 0.5, 0.5)
            .chop(config, 2)
            .djf(LFO(0.35, 0.45, 4))
//            .mute()

        + cycle(1.25, 0.75, 2.0)
            .sample(bd)
            .amp(1.0)
//            .mute()

        + repeat(2.0).sample(sd, at = 1.0)
            .amp(0.85)
//            .mute()

        + cycle(4)
//        + cycle(0.75, 2.0, 1.25)
            .loop(beat, 2, startBeat = 4)
            .amp(0.75)
            .waveDist(0.5)
            .mute()

        + listOf((0.5 to 2), (0.5 to 4), (0.5 to 4))
            .flatMap { (dur, i) -> (1..i).map { dur } }
            .let { cycle(it) }
            .sample(hh)
            .chop(config, 4)
            .amp(cycle(0.5, 0.75, 0.5))
//            .lpf(LFO(6000, 12500, 6))
//            .mute()


        + cycle(beats).loop("mk-vox-A", beats)
            .waveDist(1)
//            .mute()

        + cycle(beats).loop("dl-clave-bounce", beats)
            .waveDist(0.2)
            .track(1)
//            .mute()

        + repeat(beats).loop(tops, beats, startBeat = 1)
            .waveDist(0.15)
//            .chop(config, 2)
            .amp(0.4)
//            .mute()

    }

}
