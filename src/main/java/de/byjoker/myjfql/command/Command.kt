package de.byjoker.myjfql.command

import org.jline.reader.ParsedLine
import java.util.stream.Collectors
import java.util.stream.IntStream

abstract class Command(val name: String, val syntax: MutableList<String>) {

    abstract fun execute(sender: CommandSender, args: MutableMap<String, MutableList<String>>)

    open fun complete(sender: CommandSender, line: ParsedLine): MutableList<String>? = null

    fun formatString(strings: MutableList<String>?): String? {
        if (strings == null) {
            return null
        }

        return if (strings.isEmpty()) null else IntStream.range(1, strings.size).mapToObj { i: Int -> " " + strings[i] }
            .collect(Collectors.joining("", strings[0], "")).replace("'", "")
    }

    fun formatList(strings: MutableList<String>?): MutableList<String>? {
        if (strings == null) {
            return null
        }

        return if (strings.isEmpty()) ArrayList() else strings.stream().map { s: String -> s.replace("'", "") }
            .collect(Collectors.toList())
    }

    fun formatInteger(strings: MutableList<String>?): Int {
        if (strings == null) return -1

        return if (strings.isEmpty()) -1 else IntStream.range(1, strings.size).mapToObj { i: Int -> " " + strings[i] }
            .collect(Collectors.joining("", strings[0], "")).replace("'", "").toInt()
    }
}
