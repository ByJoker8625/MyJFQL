package de.byjoker.myjfql.user

import de.byjoker.myjfql.database.DatabasePermissionLevel

interface User {

    companion object Permissions {
        const val ALLOW_CREATE_DATABASES = "%allow_create_databases%"
    }

    var id: String
    var name: String
    var password: String
    var accesses: MutableMap<String, DatabasePermissionLevel>
    var preferredDatabaseId: String?
    fun allowed(databaseId: String, type: DatabasePermissionLevel): Boolean
    fun grantAccess(databaseId: String, type: DatabasePermissionLevel)
    fun revokeAccess(databaseId: String)
    fun hasPreferredDatabase(): Boolean
    fun validPassword(password: String): Boolean
}
