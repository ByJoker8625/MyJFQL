package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.user.Permission
import de.byjoker.myjfql.user.UserType
import org.jline.reader.ParsedLine
import kotlin.system.exitProcess

@CommandHandler
class ShutdownCommand : Command("shutdown", listOf("command", "hardly"), listOf("stop", "exit")) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
        if (!sender.permitted(Permission.SHUTDOWN)) {
            sender.forbidden()
            return
        }

        if (args.containsKey("hardly")) {
            exitProcess(0)
        }

        MyJFQL.getInstance().shutdown()
    }

    override fun complete(sender: CommandSender, line: ParsedLine): List<String>? {
        return when {
            line.wordIndex() == 1 -> listOf("hardly")
            else -> null
        }
    }

}
