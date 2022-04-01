package de.byjoker.myjfql.network.cluster

import de.byjoker.myjfql.util.Range

data class ClusterWorker(val id: String, val range: Range, val address: String, val port: Int, val token: String)
