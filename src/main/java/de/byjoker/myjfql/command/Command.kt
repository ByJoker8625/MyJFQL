package de.byjoker.myjfql.command

import org.jline.reader.ParsedLine
import java.util.stream.Collectors
import java.util.stream.IntStream

abstract class Command(val name: String, val syntax: List<String>) {

    abstract fun execute(sender: CommandSender, args: Map<String, List<String>>)

    open fun complete(sender: CommandSender, line: ParsedLine): List<String>? = null

    fun formatString(strings: List<String>?): String? {
        if (strings == null) {
            return null
        }

        return if (strings.isEmpty()) null else IntStream.range(1, strings.size).mapToObj { i: Int -> " " + strings[i] }
            .collect(Collectors.joining("", strings[0], "")).replace("'", "")
    }

    fun formatList(strings: List<String>?): List<String>? {
        if (strings == null) {
            return null
        }

        return if (strings.isEmpty()) listOf() else strings.stream().map { s: String -> s.replace("'", "") }.toList()
    }

    fun formatInteger(strings: List<String>?): Int {
        if (strings == null) return -1

        return if (strings.isEmpty()) -1 else IntStream.range(1, strings.size).mapToObj { i: Int -> " " + strings[i] }
            .collect(Collectors.joining("", strings[0], "")).replace("'", "").toInt()
    }
}
