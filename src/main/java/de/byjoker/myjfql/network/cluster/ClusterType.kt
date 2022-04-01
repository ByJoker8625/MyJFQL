package de.byjoker.myjfql.network.cluster

enum class ClusterType {

    DUPLICATED,
    SHARDED,
    DISABLED;

    companion object {
        const val FULLY_SHARDED = 3
        const val TABLES_SHARDED = 2
        const val DATABASES_SHARDED = 1
    }

}
