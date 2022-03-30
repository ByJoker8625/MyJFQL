package de.byjoker.myjfql.util

class NoneEncryptor : Encryptor {

    override val name = "NONE"
    override fun encrypt(s: String) = s

}
