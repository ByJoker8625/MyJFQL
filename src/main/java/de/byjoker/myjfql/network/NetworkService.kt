package de.byjoker.myjfql.network

import de.byjoker.myjfql.network.controller.Controller

interface NetworkService {

    fun start(port: Int)

    fun shutdown()

    fun registerController(controller: Controller)

    fun unregisterController(controller: Controller)

    val controllers: MutableList<Controller>

}
