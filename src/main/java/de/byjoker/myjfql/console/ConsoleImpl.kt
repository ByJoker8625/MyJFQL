package de.byjoker.myjfql.console

import de.byjoker.myjfql.exception.ConsoleException
import org.jline.reader.Completer
import org.slf4j.LoggerFactory
import java.util.*

class ConsoleImpl : Console {

    private val logger = LoggerFactory.getLogger("de.byjoker.myjfql")
    private val scanner = Scanner(System.`in`)

    override fun info(message: String) {
        logger.info(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }

    override fun error(exception: Throwable) {
        logger.error(exception.message, exception)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }

    override fun debug(message: String) {
        logger.debug(message)
    }

    override fun bind(completer: Completer) {
    }

    override fun readPrompt(): String {
        return scanner.nextLine()
    }

    override fun clear() {
        throw ConsoleException("The standard java console doesn't offer any method for clearing the screen!")
    }

}
