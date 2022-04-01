package de.byjoker.myjfql.network.cluster

interface Cluster {

    fun join()
    fun quit()
    fun registerWorker(worker: ClusterWorker)
    fun unregisterWorker(id: String)
    fun getWorkers(): List<ClusterWorker>
    suspend fun query(query: String, worker: String)
    suspend fun balance(query: String)
    suspend fun sync(query: String)
    fun shutdown()

}
