package de.byjoker.myjfql.user

import com.fasterxml.jackson.annotation.JsonIgnore
import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel
import de.byjoker.myjfql.util.IDGenerator

class SimpleUser(
    override var id: String = IDGenerator.generateDigits(8),
    override var name: String,
    override var password: String,
    override var accesses: MutableMap<String, DatabasePermissionLevel> = HashMap(),
    override var preferredDatabaseId: String? = null
) : User {

    override fun allowed(databaseId: String, type: DatabasePermissionLevel): Boolean {
        return (accesses.containsKey("*") && accesses["*"]!!.can(type)) || (accesses.containsKey(databaseId) && accesses[databaseId]!!.can(
            type
        ))
    }

    override fun grantAccess(databaseId: String, type: DatabasePermissionLevel) {
        accesses[databaseId] = type
    }

    override fun revokeAccess(databaseId: String) {
        accesses.remove(databaseId)
    }

    @JsonIgnore
    override fun hasPreferredDatabase(): Boolean {
        return preferredDatabaseId != null
    }

    override fun validPassword(password: String): Boolean {
        return password == MyJFQL.getInstance().encryptor.encrypt(password)
    }

}
