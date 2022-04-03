package de.byjoker.myjfql.console

import org.jline.reader.Completer

interface Console {

    fun info(message: String)
    fun error(message: String)
    fun warn(message: String)
    fun bind(completer: Completer)
    fun readPrompt(): String
    fun clear()

}
