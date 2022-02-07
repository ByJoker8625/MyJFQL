package de.byjoker.myjfql.console

import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.LineReaderImpl
import org.jline.widget.AutosuggestionWidgets
import java.io.PrintStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*


class SimpleConsole(
    private val reader: LineReaderImpl = LineReaderBuilder.builder().build() as LineReaderImpl,
    private val writer: PrintWriter = reader.terminal.writer(),
    private var writeable: Boolean = false
) : Console {

    init {
        reader.setPrompt("")

        System.setErr(object : PrintStream(System.err) {
            override fun print(s: String) {
                this@SimpleConsole.print("[ERROR] $s")
            }

            override fun println(s: String) {
                print("$s\n")
            }
        })

        System.setOut(object : PrintStream(System.out) {
            override fun print(s: String) {
                this@SimpleConsole.print(s)
            }

            override fun println(s: String) {
                print("$s\n")
            }
        })
    }

    override fun clean() {
        println(null);
    }

    override fun log(s: String) {
        println(s)
    }

    override fun logInfo(s: String) {
        log("[INFO] $s")
    }

    override fun logWarning(s: String) {
        log("[WARNING] $s")
    }

    override fun logError(s: String) {
        log("[ERROR] $s")
    }

    override fun print(s: String?) {
        if (s == null) {
            writer.print("")
        } else {
            writer.print("[${time()}] $s")

            if (writeable && reader.isReading) {
                reader.callWidget(LineReader.REDRAW_LINE);
                reader.callWidget(LineReader.REDISPLAY);
                reader.terminal.writer().flush();
            }

        }

        writer.flush()
    }

    override fun println(s: String?) {
        when (s) {
            null -> {
                writer.println()
            }
            else -> {
                print("$s\n")
            }
        }
    }

    override fun readPrompt(): String {
        try {
            return reader.readLine()
        } catch (ignore: Exception) {
        }

        return ""
    }

    override fun bind(vararg parameters: Any) {
        reader.completer = parameters[0] as Completer

        val autosuggestionWidgets = AutosuggestionWidgets(reader)
        reader.putString("shutdown")
        autosuggestionWidgets.enable()

        writeable = true
    }

    override fun clear() {
        reader.clearScreen()
    }

    private fun time(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    }

}
