package de.byjoker.myjfql.user

import de.byjoker.myjfql.core.MyJFQL
import de.byjoker.myjfql.database.DatabasePermissionLevel

abstract class User(
    var id: String,
    var name: String,
    var password: String,
    var accesses: MutableMap<String, DatabasePermissionLevel>,
    var preferredDatabaseId: String?
) {

    companion object Permissions {
        const val ALLOW_CREATE_DATABASES = "%allow_create_databases%"
    }

    fun allowed(databaseId: String, type: DatabasePermissionLevel): Boolean {
        return (accesses.containsKey("*") && accesses["*"]!!.can(type)) || (accesses.containsKey(databaseId) && accesses[databaseId]!!.can(
            type
        ))
    }

    fun grantAccess(databaseId: String, type: DatabasePermissionLevel) = accesses.put(databaseId, type)

    fun revokeAccess(databaseId: String) = accesses.remove(databaseId)

    fun validPassword(password: String): Boolean = this.password == MyJFQL.getInstance().encryptor.encrypt(password)


    override fun toString(): String {
        return "User(id='$id', name='$name', password='$password', accesses=$accesses, preferredDatabaseId=$preferredDatabaseId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
