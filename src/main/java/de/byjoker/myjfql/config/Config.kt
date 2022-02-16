package de.byjoker.myjfql.config

data class ServerConfig(
    var enabled: Boolean = true,
    var trusted: List<String> = listOf("http://localhost"),
    var port: Int = 2291
)

data class RegistryConfig(
    var host: String = "https://cdn.byjoker.de/myjfql/myjfql.json",
    var lookup: Boolean = true,
    var autoUpdates: Boolean = false,
    var output: String = "MyJFQL.jar"
)

data class Config(
    var server: ServerConfig = ServerConfig(),
    var docker: Boolean = false,
    var encryption: String = "ARGON2",
    var registry: RegistryConfig = RegistryConfig(),
    var jline: Boolean = true
)
