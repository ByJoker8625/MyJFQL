package de.byjoker.myjfql.util

interface Encryptor {
    val name: String
    fun encrypt(s: String): String?
}
