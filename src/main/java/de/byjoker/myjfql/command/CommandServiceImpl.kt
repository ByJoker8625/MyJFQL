package de.byjoker.myjfql.command

import de.byjoker.myjfql.lang.Interpreter
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors


class CommandServiceImpl(private var commands: MutableList<Command> = mutableListOf()) : CommandService {

    override fun registerCommand(command: Command) {
        commands.add(command)
    }

    override fun unregisterCommand(name: String) {
        commands.removeIf { command -> command.name.equals(name, ignoreCase = true) }
    }

    override fun searchCommands(directory: String) {
        for (clazz in findAllClassesUsingClassLoader(directory)) {
            if (clazz.isInstance(Command::class)) registerCommand(
                (clazz as Class<Command>).getDeclaredConstructor().newInstance()
            )
        }
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

    private fun findAllClassesUsingClassLoader(packageName: String): List<Class<*>> {
        val stream: InputStream =
            ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replace("[.]".toRegex(), "/"))
        val reader = BufferedReader(InputStreamReader(stream))
        return reader.lines().filter { line: String -> line.endsWith(".class") }.map { line: String ->
            getClass(
                line, packageName
            )
        }.collect(Collectors.toSet()).filter { clazz -> clazz.isAnnotationPresent(CommandHandler::class.java) }
    }

    private fun getClass(className: String, packageName: String): Class<*> {
        return Class.forName(
            packageName + "." + className.substring(0, className.lastIndexOf('.'))
        )
    }

}
