package de.byjoker.myjfql.user

import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.util.IDGenerator

class SimpleUser(
    id: String = IDGenerator.generateDigits(8),
    name: String,
    password: String,
    accesses: MutableMap<String, DatabasePermissionLevel> = mutableMapOf(),
    preferredDatabaseId: String? = null
) : User(id, name, password, accesses, preferredDatabaseId)
