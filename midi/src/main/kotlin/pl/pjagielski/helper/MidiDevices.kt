package pl.pjagielski.helper

import javax.sound.midi.MidiSystem

fun main() {
    MidiSystem.getMidiDeviceInfo().toList()
        .map { MidiSystem.getMidiDevice(it) }
        .map { it.deviceInfo.description }
        .also(::println)
}
