package de.byjoker.myjfql.config

data class RegistryConfig(
    val host: String = "https://cdn.byjoker.de/myjfql/myjfql.json",
    val lookup: Boolean = true,
    val autoUpdates: Boolean = false,
    val output: String = "MyJFQL.jar"
)
