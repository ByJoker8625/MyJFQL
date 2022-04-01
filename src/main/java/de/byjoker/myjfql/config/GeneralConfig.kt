package de.byjoker.myjfql.config

data class GeneralConfig(
    val server: ServerConfig = ServerConfig(),
    val cluster: ClusterConfig = ClusterConfig(),
    val docker: Boolean = false,
    val encryption: String = "ARGON2",
    val advancedCaching: Boolean = true,
    val registry: RegistryConfig = RegistryConfig(),
    val jline: Boolean = true
)
