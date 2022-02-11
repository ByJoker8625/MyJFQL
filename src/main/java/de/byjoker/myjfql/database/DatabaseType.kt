package de.byjoker.myjfql.database

import java.util.*

enum class DatabaseType(vararg aliases: String) {
    SINGLETON("FILE", "SINGLETON", "SINGLE"), SPLIT("FOLDER", "SPLIT", "MULTIPLE");

    private val aliases: List<String> = listOf(*aliases)

    companion object {
        fun likeDatabaseType(like: String): DatabaseType? {
            return Arrays.stream(values())
                .filter { databaseType: DatabaseType? -> databaseType!!.aliases.contains(like.uppercase(Locale.getDefault())) }
                .findFirst()
                .orElse(null)
        }
    }
}
