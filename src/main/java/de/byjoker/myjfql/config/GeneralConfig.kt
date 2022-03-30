package de.byjoker.myjfql.config

import de.byjoker.myjfql.util.IDGenerator

data class GeneralConfig(
    val server: ServerConfig = ServerConfig(),
    val uniqueId: String = IDGenerator.generateMixed(25),
    val cluster: ClusterConfig = ClusterConfig(),
    val docker: Boolean = false,
    val encryption: String = "ARGON2",
    val registry: RegistryConfig = RegistryConfig(),
    val jline: Boolean = true
)
