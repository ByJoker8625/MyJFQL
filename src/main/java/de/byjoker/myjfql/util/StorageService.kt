package de.byjoker.myjfql.util

import java.io.File

interface StorageService<T> {

    suspend fun load(backend: File): T?
    fun loadAll()
    suspend fun write(backed: File, t: T)
    fun writeAll()

}
