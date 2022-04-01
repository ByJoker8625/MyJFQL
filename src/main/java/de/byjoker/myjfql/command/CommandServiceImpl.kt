package de.byjoker.myjfql.command

import de.byjoker.myjfql.lang.Interpreter

class CommandServiceImpl(private var commands: MutableList<Command> = mutableListOf()) : CommandService {

    override fun registerCommand(command: Command) {
        commands.add(command)
    }

    override fun unregisterCommand(name: String) {
        commands.removeIf { command -> command.name.equals(name, ignoreCase = true) }
    }

    override fun getCommand(name: String): Command? {
        return commands.firstOrNull { command -> command.name.equals(name, ignoreCase = true) }
    }

    override fun getCommands(): List<Command> = commands

    override fun execute(sender: CommandSender, query: String, interpreter: Interpreter) {
        try {
            val arguments = interpreter.interpretCommand(query)

            if (!arguments.containsKey("command")) {
                sender.error("Command wasn't defined!")
                return
            }

            val command = getCommand(arguments["command"]!![0])

            if (command == null) {
                sender.error("Command wasn't found!")
                return
            }

            command.execute(sender, arguments)
        } catch (ex: Exception) {
            sender.error(ex)
        }
    }
}
