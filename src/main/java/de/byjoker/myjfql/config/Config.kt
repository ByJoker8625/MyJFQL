package de.byjoker.myjfql.config

class Config {
    var server: ServerConfig = ServerConfig(true, 2291)
    var isDocker = false
    var encryption: String = "ARGON2"
    var registry: RegistryConfig =
        RegistryConfig("https://cdn.byjoker.de/myjfql/myjfql.json", true, false, "MyJFQL.jar")
    var isJline = false
}
