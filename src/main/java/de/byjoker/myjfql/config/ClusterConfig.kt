package de.byjoker.myjfql.config

import de.byjoker.myjfql.network.util.ClusterRole
import de.byjoker.myjfql.network.util.ClusterType
import de.byjoker.myjfql.util.IDGenerator

data class ClusterConfig(
    val uniqueId: String = "${IDGenerator.generateString(5)}${IDGenerator.generateDigits(2)}",
    val role: ClusterRole = ClusterRole.STANDALONE,
    val type: ClusterType = ClusterType.DISABLED,
    val level: Int = -1,
    val superior: String = "",
    val auth: List<String> = listOf()
)
