package de.byjoker.myjfql.user

import de.byjoker.myjfql.database.DatabasePermissionLevel

interface User {

    val id: String
    val name: String
    val type: UserType
    var locked: Boolean
    var password: String
    var preferredDatabaseId: String?
    val permissions: MutableMap<String, DatabasePermissionLevel>
    fun validPassword(password: String): Boolean
    fun permitted(permission: Permission): Boolean = type.permitted(permission)
    fun permitted(action: DatabasePermissionLevel, databaseId: String): Boolean
    fun grantAccess(action: DatabasePermissionLevel, databaseId: String)
    fun revokeAccess(databaseId: String)

}
