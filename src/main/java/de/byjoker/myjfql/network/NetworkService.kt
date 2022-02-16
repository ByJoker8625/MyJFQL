package de.byjoker.myjfql.network

import de.byjoker.myjfql.network.controller.Controller

interface NetworkService {

    fun registerController(controller: Controller)

    fun unregisterController(controller: Controller)

    fun start(port: Int)

    fun shutdown()

    val controllers: MutableList<Controller>

}
