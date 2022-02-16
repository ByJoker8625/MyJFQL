package de.byjoker.myjfql.console

import de.byjoker.myjfql.command.CommandSender
import de.byjoker.myjfql.command.CommandService
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

class ConsoleCommandCompleter(private val commandService: CommandService, private val sender: CommandSender) :
    Completer {

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        when (line.wordIndex()) {
            0 -> {
                commandService.commands.forEach { command ->
                    candidates.add(Candidate(command.name))
                }
            }
            else -> {
                val command = commandService.getCommand(line.words()[0]) ?: return
                val completions = command.complete(sender, line)

                if (completions != null)
                    candidates.addAll(0, completions.map { completion -> Candidate(completion) })
            }
        }
    }

}
