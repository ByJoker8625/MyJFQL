package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.lang.Interpreter

interface CommandService {

    fun registerCommand(command: Command)
    fun unregisterCommand(name: String)
    fun getCommand(name: String): Command?
    fun getCommands(): List<Command>
    fun execute(sender: CommandSender, query: String, interpreter: Interpreter = MyJFQL.getInstance().interpreter)

}
