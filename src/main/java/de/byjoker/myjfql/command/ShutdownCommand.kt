package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.user.UserType
import kotlin.system.exitProcess

class ShutdownCommand : Command("shutdown", listOf("command", "hardly"), listOf("stop", "exit")) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
        if (!sender.permitted(UserType.MANAGER)) {
            sender.forbidden()
            return
        }

        if (args.containsKey("hardly")) {
            exitProcess(0)
        }

        MyJFQL.getInstance().shutdown()
    }

}
