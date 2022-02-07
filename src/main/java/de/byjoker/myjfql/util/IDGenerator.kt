package de.byjoker.myjfql.util

import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

object IDGenerator {

    private val random = Random()

    private fun generate(letters: String, length: Int): String {
        return IntStream.range(0, length).mapToObj { letters[random.nextInt(letters.length)].toString() }
            .collect(Collectors.joining())
    }

    @JvmStatic
    fun generateMixed(length: Int): String {
        return generate("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", length)
    }

    @JvmStatic
    fun generateString(length: Int): String {
        return generate("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", length)
    }

    @JvmStatic
    fun generateDigits(length: Int): String {
        return generate("0123456789", length)
    }
}
