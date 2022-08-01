package pl.pjagielski.tracker.model

data class Note(
    val beat: Double,
    val midinote: Int,
    val duration: Double,
    val amp: Float = 1.0f
)
