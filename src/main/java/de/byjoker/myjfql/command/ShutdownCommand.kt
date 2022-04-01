package de.byjoker.myjfql.command

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.user.UserType

class ShutdownCommand : Command("shutdown", listOf("command"), listOf("stop", "exit")) {

    override fun execute(sender: CommandSender, args: Map<String, List<String>>) {
        if (!sender.permitted(UserType.MANAGER)) {
            sender.forbidden()
            return
        }

        MyJFQL.getInstance().shutdown()
    }

}
