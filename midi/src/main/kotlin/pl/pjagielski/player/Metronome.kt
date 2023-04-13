package pl.pjagielski.player

data class Metronome(val bpm: Int, val beatsPerBar: Int) {
    val millisPerBeat: Long
        get() = (secsPerBeat * 1000).toLong()

    val millisPerBar: Long
        get() = beatsPerBar * millisPerBeat

    private val secsPerBeat: Double
        get() = 60.0 / bpm
}
