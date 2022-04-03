package de.byjoker.myjfql.console

import de.byjoker.myjfql.exception.ConsoleException
import org.jline.reader.Completer

class ConsoleImpl : Console {

    override fun info(message: String) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }

    override fun warn(message: String) {
        TODO("Not yet implemented")
    }

    override fun bind(completer: Completer) {
        TODO("Not yet implemented")
    }

    override fun readPrompt(): String {
        TODO("Not yet implemented")
    }

    override fun clear() {
        throw ConsoleException("The standard java console doesn't offer any method for clearing the screen!")
    }

}
