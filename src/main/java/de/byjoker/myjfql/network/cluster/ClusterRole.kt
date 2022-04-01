package de.byjoker.myjfql.network.cluster

enum class ClusterRole(val clusterType: ClusterType?) {

    LOADBALANCER(null), STANDALONE(null), MASTER(ClusterType.DUPLICATED), SLAVE(ClusterType.DUPLICATED), SHARD(
        ClusterType.SHARDED
    )

}
