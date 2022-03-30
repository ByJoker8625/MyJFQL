package de.byjoker.myjfql.util

import de.mkammerer.argon2.Argon2Factory
import java.nio.charset.StandardCharsets

class Argon2Encryptor(private val salt: String) : Encryptor {


    private val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id)
    override val name: String = "ARGON2"

    override fun encrypt(s: String) =
        String(argon2.rawHash(10, 1576, 4, salt.toCharArray(), s.toByteArray(StandardCharsets.UTF_8)))

}
