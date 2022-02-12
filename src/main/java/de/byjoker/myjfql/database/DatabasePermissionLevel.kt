package de.byjoker.myjfql.database

enum class DatabasePermissionLevel(private val level: Int) {

    NONE(0), READ(1), READ_WRITE(2);

    fun can(action: DatabasePermissionLevel): Boolean {
        return level >= action.level
    }
}
