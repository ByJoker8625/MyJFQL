package de.byjoker.myjfql.config

import de.byjoker.myjfql.network.cluster.ClusterMemberType

data class ClusterConfig(
    val enabled: Boolean = false,
    val type: ClusterMemberType = ClusterMemberType.MANAGER,
    val manager: String = "",
    val token: String = ""
)
