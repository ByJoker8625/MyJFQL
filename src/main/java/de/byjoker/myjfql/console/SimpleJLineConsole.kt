package de.byjoker.myjfql.console

import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.LineReaderImpl
import org.jline.widget.AutosuggestionWidgets
import java.io.PrintStream

class SimpleJLineConsole(private val console: Console) : Console {

    private val reader: LineReaderImpl = LineReaderBuilder.builder().build() as LineReaderImpl

    init {
        reader.setPrompt("")

        System.setErr(object : PrintStream(System.err) {
            override fun print(s: String?) {
                if (reader.isReading) {
                    reader.callWidget(LineReader.REDRAW_LINE)
                    reader.callWidget(LineReader.REDISPLAY)
                    reader.terminal.writer().flush()
                }
            }

            override fun println(s: String?) {
                if (reader.isReading) {
                    reader.callWidget(LineReader.REDRAW_LINE)
                    reader.callWidget(LineReader.REDISPLAY)
                    reader.terminal.writer().flush()
                }
            }
        })
    }

    override fun info(message: String) {
        console.info(message)
    }

    override fun error(message: String) {
        console.error(message)
    }

    override fun error(exception: Throwable) {
        console.error(exception)
    }

    override fun warn(message: String) {
        console.warn(message)
    }

    override fun debug(message: String) {
        console.debug(message)
    }

    override fun bind(completer: Completer) {
        reader.completer = completer
        AutosuggestionWidgets(reader).enable()
    }

    override fun readPrompt(): String {
        return reader.readLine()
    }

    override fun clear() {
        reader.clearScreen()
    }

}
