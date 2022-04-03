package de.byjoker.myjfql.config

import de.byjoker.myjfql.util.IDGenerator

data class GeneralConfig(
    val server: ServerConfig = ServerConfig(),
    val cluster: ClusterConfig = ClusterConfig(),
    val docker: Boolean = false,
    val encryption: String = "ARGON2",
    val salt: String = IDGenerator.generateString(5),
    val advancedCaching: Boolean = true,
    val registry: RegistryConfig = RegistryConfig(),
    val jline: Boolean = true
)
