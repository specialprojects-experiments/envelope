package com.specialprojects.experiments.envelopecall.audio

import kotlin.math.sin

object DTMF {
    /**
     * The list of valid DTMF frequencies. See the [WikiPedia article on DTMF](http://en.wikipedia.org/wiki/Dual-tone_multi-frequency_signaling).
     */
    val DTMF_FREQUENCIES =
        doubleArrayOf(697.0, 770.0, 852.0, 941.0, 1209.0, 1336.0, 1477.0, 1633.0)
    /**
     * The list of valid DTMF characters. See the [WikiPedia article on DTMF](http://en.wikipedia.org/wiki/Dual-tone_multi-frequency_signaling) for the relation between the characters
     * and frequencies.
     */
    val DTMF_CHARACTERS = arrayOf(
        charArrayOf('1', '2', '3', 'A'),
        charArrayOf('4', '5', '6', 'B'),
        charArrayOf('7', '8', '9', 'C'),
        charArrayOf('*', '0', '#', 'D')
    )

    /**
     * Generate a DTMF - tone for a valid DTMF character.
     * @param character a valid DTMF character (present in DTMF_CHARACTERS}
     * @return a float buffer of predefined length (7168 samples) with the correct DTMF tone representing the character.
     */
    fun generateDTMFTone(character: Char): DoubleArray {
        var firstFrequency = -1.0
        var secondFrequency = -1.0
        for (row in DTMF_CHARACTERS.indices) {
            for (col in DTMF_CHARACTERS[row].indices) {
                if (DTMF_CHARACTERS[row][col] == character) {
                    firstFrequency = DTMF_FREQUENCIES[row]
                    secondFrequency = DTMF_FREQUENCIES[col + 4]
                }
            }
        }
        return audioBufferDTMF(
            firstFrequency,
            secondFrequency,
            512 * 2 * 10
        )
    }

    /**
     * Checks if the given character is present in DTMF_CHARACTERS.
     *
     * @param character
     * the character to check.
     * @return True if the given character is present in
     * DTMF_CHARACTERS, false otherwise.
     */
    fun isDTMFCharacter(character: Char): Boolean {
        var firstFrequency = -1.0
        var secondFrequency = -1.0
        for (row in DTMF_CHARACTERS.indices) {
            for (col in DTMF_CHARACTERS[row].indices) {
                if (DTMF_CHARACTERS[row][col] == character) {
                    firstFrequency = DTMF_FREQUENCIES[row]
                    secondFrequency = DTMF_FREQUENCIES[col + 4]
                }
            }
        }
        return firstFrequency != -1.0 && secondFrequency != -1.0
    }

    /**
     * Creates an audio buffer in a float array of the defined size. The sample
     * rate is 44100Hz by default. It mixes the two given frequencies with an
     * amplitude of 0.5.
     *
     * @param f0
     * The first fundamental frequency.
     * @param f1
     * The second fundamental frequency.
     * @param size
     * The size of the float array (sample rate is 44.1kHz).
     * @return An array of the defined size.
     */
    fun audioBufferDTMF(
        f0: Double, f1: Double,
        size: Int
    ): DoubleArray {
        val sampleRate = 44100.0
        val amplitudeF0 = 0.4
        val amplitudeF1 = 0.4
        val twoPiF0 = 2 * Math.PI * f0
        val twoPiF1 = 2 * Math.PI * f1
        val buffer = DoubleArray(size)
        for (sample in buffer.indices) {
            val time = sample / sampleRate
            val f0Component = amplitudeF0 * sin(twoPiF0 * time)
            val f1Component = amplitudeF1 * sin(twoPiF1 * time)
            buffer[sample] = (f0Component + f1Component)
        }
        return buffer
    }
}