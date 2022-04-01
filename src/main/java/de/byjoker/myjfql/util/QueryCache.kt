package de.byjoker.myjfql.util

import de.byjoker.myjfql.network.util.Response

class QueryCache : Cache<String, Response> {

    override fun cache(query: String, entry: Response) {
        TODO("Not yet implemented")
    }

    override fun cache(query: String): Response? {
        TODO("Not yet implemented")
    }

    override fun getCache(): Map<String, Response> {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun clear(by: String) {
        TODO("Not yet implemented")
    }

}
