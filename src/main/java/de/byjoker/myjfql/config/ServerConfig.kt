package de.byjoker.myjfql.config

data class ServerConfig(
    val enabled: Boolean = true,
    val port: Int = 2291,
    val trusted: List<String> = listOf("localhost", "127.0.0.1")
)
