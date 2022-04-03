package de.byjoker.myjfql.util

import java.io.File

interface StorageService<T> {

    fun load(backend: File): T?
    fun loadAll()
    fun write(backed: File, t: T)
    fun writeAll()

}
