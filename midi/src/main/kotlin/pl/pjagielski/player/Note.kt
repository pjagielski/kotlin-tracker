package pl.pjagielski.player

data class Note(
    val beat: Double,      // e.g. 0.0, 0.25, 1.5
    val midinote: Int,     // e.g. 60, 57
    val duration: Double,  // in beats: e.g. 0.25, 0.5
    val amp: Float = 1.0f  // 0.0f - 1.0f
)
