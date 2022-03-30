package de.byjoker.myjfql.command

import org.jline.reader.ParsedLine

abstract class Command(val name: String, val keywords: List<String>, val aliases: List<String>) {

    abstract fun execute(sender: CommandSender, args: Map<String, String>)

    open fun complete(sender: CommandSender, line: ParsedLine): List<String>? = null

}
