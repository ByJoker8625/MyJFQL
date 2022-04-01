package de.byjoker.myjfql.util

interface Cache<K, V> {

    fun cache(query: K, entry: V)
    fun cache(query: K): V?
    fun getCache(): Map<K, V>
    fun clear(by: K)
    fun clear()

}
