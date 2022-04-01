package de.byjoker.myjfql.util

import java.io.File

interface StorageService {

    fun load(backend: File)
    fun loadAll()
    fun save(backed: File)
    fun saveAll()

}
